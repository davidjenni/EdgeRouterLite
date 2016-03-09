#!/bin/bash

PARTED=$( which parted )

W_DIR=w
# SERVER=[web server where you store firmwares]
KERNEL_ORIG=vmlinux.tmp
KERNEL_ORIG_MD5=vmlinux.tmp.md5
KERNEL=vmlinux.64
SQUASHFS_ORIG=squashfs.tmp
SQUASHFS_MD5_ORIG=squashfs.tmp.md5
SQUASHFS=squashfs.img
VERSION_ORIG=version.tmp

find_usb_key () {
        # step 1 : count usb devices
        nb=0
        disk=''
        for dev in $( ls -d /sys/block/sd* ) ; do
                if [ -h ${dev} ] ; then
                        usb=$( readlink ${dev} | grep 'usb' | wc -l )
                        if [ "${usb}" == "1" ] ; then
                                disk=${dev}
                                ((++nb))
                                if [ ${nb} -gt 1 ] ; then
                                        disk=''
                                fi
                        fi
                fi
        done
        echo "${disk}"
        if [ ${nb} -eq 1 ] ; then
                nb=0
        else
                if [ ${nb} -eq 0 ] ; then
                        nb=1
                fi
        fi
        return ${nb}
}

# key=$( find_usb_key )
key=/sys/block/sdb
nbkeys=$?

if [ ${nbkeys} != 0 ] ; then
        # fatal error, none found or more than one...
        echo "FATAL: ${nbkeys} usb keys found... I only work with one"
        exit 1
fi

key=$( echo ${key} | sed -e "s/^\/sys\/block\(\/.*\)$/\/dev\1/" )

echo key is: ${key}...


BOOT=${key}1
ROOT=${key}2
BOOT_MNT_DIR=/tmp${BOOT}
ROOT_MNT_DIR=/tmp${ROOT}

if [ "$1" != "--skip-part" ] ; then

        mounts=$( grep ${key} /proc/mounts | wc -l )

        if [ ${mounts} -gt 0 ] ; then
                echo "Forcibly unmounting stuff"
                for dev in $( grep ${key} /proc/mounts | sed -e 's/^\([^\ ]*\)\ .*$/\1/' ) ; do
                        echo "unmounting ${dev}"
                        umount ${dev}
                done
        fi

        echo "re-creating the partition table"
        ${PARTED} --script ${key} mktable msdos

        echo "creating boot partition"
        ${PARTED} --script ${key} -- mkpart primary fat32 1 150MB
        echo "creating root partition"
        ${PARTED} --script ${key} -- mkpart primary ext3 150MB -1s

        echo "formatting boot partition"
        mkfs.vfat ${BOOT}
        echo "formatting root partition"
        echo "y" | mkfs.ext3 -v ${ROOT}
fi

mkdir -p ${BOOT_MNT_DIR} ${ROOT_MNT_DIR}
echo "mounting boot partition ${BOOT} to: {BOOT_MNT_DIR}"
mount -t vfat ${BOOT} ${BOOT_MNT_DIR}
echo "mounting root partition ${ROOT} to: {ROOT_MNT_DIR}"
mount -t ext3 ${ROOT} ${ROOT_MNT_DIR}

TMP_DIR=/tmp/erl-install
mkdir -p ${TMP_DIR}

# echo "Downloading the tar file"
# curl -o ${TMP_DIR}/edgeos.tar ${SERVER}/erl/latest
cp /home/david/Downloads/EdgeRouterLite/ER-e100.v1.8.0.4853089.tar  ${TMP_DIR}/edgeos.tar

echo "Unpacking EdgeOS release image"
tar xf ${TMP_DIR}/edgeos.tar -C ${TMP_DIR}

echo "Verifying EdgeOS kernel"
if [ `md5sum ${TMP_DIR}/${KERNEL_ORIG} | awk -F ' ' '{print $1}'` != `cat ${TMP_DIR}/${KERNEL_ORIG_MD5}` ]; then
        echo "Kernel from your image is corrupted! Check your image and start over."
        exit 1
fi

echo "Copying EdgeOS kernel to boot partition"
cp ${TMP_DIR}/${KERNEL_ORIG} ${BOOT_MNT_DIR}/${KERNEL}
cp ${TMP_DIR}/${KERNEL_ORIG_MD5} ${BOOT_MNT_DIR}/${KERNEL_MD5}

# The image
echo "Verifying EdgeOS system image"
if [ `md5sum ${TMP_DIR}/${SQUASHFS_ORIG} | awk -F ' ' '{print $1}'` != `cat ${TMP_DIR}/${SQUASHFS_MD5_ORIG}` ]; then
        echo "System image from your image is corrupted! Check your image and start over."
        exit 1
fi

echo "Copying EdgeOS system image to root partition"
mv ${TMP_DIR}/${SQUASHFS_ORIG} ${ROOT_MNT_DIR}/${SQUASHFS}
mv ${TMP_DIR}/${SQUASHFS_MD5_ORIG} ${ROOT_MNT_DIR}/${SQUASHFS_MD5}

echo "Copying version file to the root partition"
mv ${TMP_DIR}/${VERSION_ORIG} ${ROOT_MNT_DIR}/${VERSION}

# Writable data dir
echo "Creating EdgeOS writable data directory"
mkdir ${ROOT_MNT_DIR}/${W_DIR}

## Cleanup
echo "Cleaning up"
rm -rf ${TMP_DIR}

echo "Installation finished"
echo "Contents of BOOT:"
ls ${BOOT_MNT_DIR}
echo "Contents of ROOT:"
ls ${ROOT_MNT_DIR}

echo "Unmounting partitions"
umount ${BOOT}
umount ${ROOT}

rm -rf ${BOOT_MNT_DIR}
rm -rf ${ROOT_MNT_DIR}

