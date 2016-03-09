firewall {
    all-ping enable
    broadcast-ping disable
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
    modify pppoe-out {
        description "TCP clamping"
        rule 1 {
            action modify
            modify {
                tcp-mss 1452
            }
            protocol tcp
            tcp {
                flags SYN
            }
        }
    }
    name eth0-in {
        default-action accept
        description "Wired network to other networks"
    }
    name eth0-local {
        default-action accept
        description "Wired network to router"
    }
    name eth1-in {
        default-action accept
        description "Wireless to other networks"
    }
    name eth1-local {
        default-action accept
        description "Wireless to router"
    }
    name pppoe-in {
        default-action drop
        description "Internet to internal networks"
        rule 1 {
            action accept
            description "Allow established/related"
            log disable
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 2 {
            action drop
            description "Drop invalid state"
            log enable
            protocol all
            state {
                invalid enable
            }
        }
    }
    name pppoe-local {
        default-action drop
        description "Internet to router"
        rule 1 {
            action accept
            description "Allow established/related"
            log disable
            protocol all
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 2 {
            action drop
            description "Drop invalid state"
            log enable
            state {
                invalid enable
            }
        }
        rule 5 {
            action accept
            description "ICMP 50/m"
            limit {
                burst 1
                rate 50/minute
            }
            log enable
            protocol icmp
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    ethernet eth0 {
        address 10.61.4.1/22
        description LAN
        duplex auto
        firewall {
            in {
                name eth0-in
            }
            local {
                name eth0-local
            }
        }
        speed auto
    }
    ethernet eth1 {
        address 10.1.1.1/22
        description Wireless
        duplex auto
        firewall {
            in {
                name eth1-in
            }
            local {
                name eth1-local
            }
        }
        speed auto
    }
    ethernet eth2 {
        duplex auto
        pppoe 0 {
            default-route force
            firewall {
                in {
                    name pppoe-in
                }
                local {
                    name pppoe-local
                }
                out {
                    modify pppoe-out
                }
            }
            mtu 1492
            name-server auto
            password aSecret
            user-id someone
        }
        speed auto
    }
    loopback lo {
    }
}
protocols {
    static {
        interface-route 0.0.0.0/0 {
            next-hop-interface pppoe0 {
            }
        }
    }
}
service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name wired-eth0 {
            authoritative disable
            description "Wired - eth1"
            subnet 10.61.4.0/22 {
                default-router 10.61.4.1
                dns-server 10.61.4.1
                lease 3600
                ntp-server 10.61.4.1
                start 10.61.4.50 {
                    stop 10.61.5.254
                }
                static-mapping david-pc {
                    ip-address 10.61.4.132
                    mac-address 90:b1:1c:73:2a:bd
                }
                static-mapping dell-1320c {
                    ip-address 10.61.4.63
                    mac-address 08:00:37:74:b6:6e
                }
                static-mapping idefix {
                    ip-address 10.61.4.200
                    mac-address 90:72:40:00:01:6F
                }
                time-server 10.61.4.1
            }
        }
        shared-network-name wireless-eth1 {
            authoritative disable
            description "Wireless - eth2"
            subnet 10.1.1.0/22 {
                default-router 10.1.1.1
                dns-server 10.1.1.1
                lease 3600
                ntp-server 10.1.1.1
                start 10.1.1.20 {
                    stop 10.1.2.254
                }
                time-server 10.1.1.1
            }
        }
    }
    dns {
        forwarding {
            cache-size 450
            listen-on eth0
            listen-on eth1
            system
        }
    }
    gui {
        https-port 443
        listen-address 10.61.4.1
    }
    nat {
        rule 5010 {
            log disable
            outbound-interface pppoe0
            protocol all
            type masquerade
        }
    }
    ssh {
        listen-address 10.61.4.1
        port 22
        protocol-version v2
    }
}
system {
    conntrack {
        expect-table-size 4096
        hash-size 4096
        table-size 32768
    }
    domain-name jenni.local
    host-name sentinel
    login {
        user ubnt {
            authentication {
                plaintext-password "ubnt"
            }
            level admin
        }
    }
    name-server 8.8.8.8
    name-server 8.8.4.4
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level debug
            }
        }
    }
    time-zone UTC
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@4:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.8.0.4853089.160219.1607 */
