# EdgeRouterLite
Config files and maintenance scripts for EdgeRouter Lite

## Intro
[EdgeRouter Lite](https://www.ubnt.com/edgemax/edgerouter-lite/) (aka ERL) is Ubiquiti's small but powerful 3 port 1GBit router;
it also happens to be [reasonably priced with < $100](http://smile.amazon.com/gp/product/B00HXT8EKE). It is NOT a plugin and run kind of router
but does require to be configured. This need for initial configuration might place an initial hurdle for newbies, but it's OS
(EdgeMAX, based on [Vyatta OS](https://en.wikipedia.org/wiki/Vyatta)) is well
supported by an active prosumer community, e.g. on the manufacturer's own [forum](http://community.ubnt.com/edgemax)
and has plenty of [KB articles](https://help.ubnt.com/hc/en-us/categories/200321064-EdgeMAX)

Firmware updates:
https://www.ubnt.com/download/edgemax

## Configuration
[This article](http://www.smallnetbuilder.com/lanwan/lanwan-howto/32014-how-to-configure-your-ubiquiti-edgerouter-lite) explains its
configuration on a couple examples.
It can be configured by a built-in web UI but its more powerful configuration is via a CLI, it can easily be reached via SSH.

After [resetting to factory defaults](https://help.ubnt.com/hc/en-us/articles/205202620-EdgeMAX-Reset-router-to-factory-defaults),
the router can be configured via its web UI at http://192.168.1.1 or via SSH. Default user and password are ````ubnt```` for both.

The erl-install.sh will work e.g. in Debian Linux, but not on OSX
(it's likely that with more [brew](http://brew.sh) packages, all missing dependencies can be added, TBD).
For now, on OSX run the erl-install from a Debian VM. The configuration can be done from any OS via SSH.

## Deployment
### Connecting via serial terminal

Connect to the serial port on the EdgeRouterLite with e.g. this [FTDI USB-serial RJ 45 adapter](http://smile.amazon.com/gp/product/B00M2SAKMG).
See [EdgeMAX - Connect to serial console port & default settings](https://help.ubnt.com/hc/en-us/articles/205202630-EdgeMAX-Connect-to-serial-console-port-default-settings)

  - discover port:
````
ls /dev/tty.* | grep -i usb
````
  - connect, e.g. on OSX:
````
screen /dev/tty.usbserial-A6041AEQ 115200
````

## Configuration
### loading config file via CLI (and SSH)
  - on factory reset ERL, configure your laptop to 192.168.1.22 for the wired LAN connection
  - connect ethernet cable between laptop and ERL
  - use scp to copy to ERL's user home directory:
````
scp config.boot.myconfig ubnt@192.168.1.1:/config/
````
  - ssh connect to ERL and run:
````
configure
load config.boot.myconfig
commit
save; exit
````
- if default user `ubnt` is still defined:
  - login via SSH as user ubnt
  - run the following config steps:
````
configure
set system login user david
set system login user david authentication plaintext-password <myPassword>
set system login user david level admin
commit
save; exit
````
  - logout of ssh connection and login again as new user
  - delete default ubnt user:
````
configure
delete system login user ubnt
commit
save; exit

````
  - logout of ssh connection and login again as ubnt; this login should now fail


## VLAN for guest access
[VLAN WalkThrough](https://help.ubnt.com/hc/en-us/articles/205197630-EdgeMAX-VLAN-Walkthrough-with-EdgeSwitch-using-Sample-Enterprise-Topology)

