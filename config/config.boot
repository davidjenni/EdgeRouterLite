firewall {
    all-ping enable
    broadcast-ping disable
    conntrack-expect-table-size 4096
    conntrack-hash-size 4096
    conntrack-table-size 32768
    conntrack-tcp-loose enable
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
    name WAN_IN {
        default-action drop
        description "packets from Internet to LAN & WLAN"
        enable-default-log
        rule 1 {
            action accept
            description "allow established sessions"
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
            description "drop invalid state"
            log disable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "packets from internet/WAN to router"
        enable-default-log
        rule 1 {
            action accept
            description "allow established session to the router"
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
            description "drop invalid state"
            log enable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
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
        speed auto
    }
    ethernet eth1 {
        address 10.1.1.1/22
        description GuestLAN2
        disable
        duplex auto
        speed auto
    }
    ethernet eth2 {
        address dhcp
        description "WAN - eth0"
        duplex auto
        speed auto
        firewall {
            in {
                name WAN_IN
            }
            local {
                name WAN_LOCAL
            }
        }
    }
    loopback lo {
    }
}
service {
    dhcp-server {
        disabled false
        shared-network-name LAN {
            authoritative disable
            description "Owner LAN"
            subnet 10.61.4.0/22 {
                default-router 10.61.4.1
                dns-server 10.61.4.1
                dns-server 8.8.8.8
                domain-name jenni.local
                lease 10800
                ntp-server 10.61.4.1
                start 10.61.4.50 {
                    stop 10.61.5.254
                }
            }
        }
        shared-network-name GuestLAN2 {
            authoritative disable
            description "Guest LAN"
            subnet 10.1.1.0/22 {
                default-router 10.1.1.1
                dns-server 8.8.8.8
                domain-name jenni.guest
                lease 86400
                start 10.1.1.50 {
                    stop 10.1.2.254
                }
            }
        }
    }
    dns {
        forwarding {
            cache-size 450
            listen-on eth0
            listen-on eth1
        }
    }
    gui {
        https-port 443
        listen-address 10.61.4.1
    }
    nat {
        rule 5000 {
            description "masquerade for WAN"
            log disable
            outbound-interface eth0
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
    domain-name jenni.local
    host-name sentinel
    login {
        user ubnt {
            authentication {
                encrypted-password "$1$zKNoUbAo$gomzUbYvgyUMcD436Wo66."
            }
            level admin
        }
    }
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
    time-zone America/Los_Angeles
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "ubnt-pptp@1:nat@3:conntrack@1:dhcp-server@4:cron@1:zone-policy@1:quagga@2:vrrp@1:system@4:qos@1:webgui@1:config-management@1:ipsec@4:firewall@5:ubnt-util@1:webproxy@1:dhcp-relay@1" === */
/* Release version: v1.7.0.4783374.150622.1534 */
