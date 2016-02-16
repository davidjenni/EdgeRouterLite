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
    modify pppoe-out {
        default-action accept
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
        description "Wired network to other networks."
    }
    name eth0-local {
        default-action accept
        description "Wired network to router."
    }
    name eth1-in {
        default-action accept
        description "Wireless network to other networks"
    }
    name eth1-local {
        default-action accept
        description "Wireless network to router."
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
                related enable
                new disable
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
                related enable
                new disable
                invalid disable
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
        firewall {
            in {
                name eth0-in
            }
            local {
                name eth0-local
            }
        }
    }
    ethernet eth1 {
        address 10.61.6.1/22
        description Wireless LAN
        firewall {
            in {
                name eth1-in
            }
            local {
                name eth1-local
            }
        }
    }
    ethernet eth2 {
        pppoe 0 {
            default-route auto
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
            password secret
            user-id joe
        }
    }
    loopback lo {
    }
}
service {
    dhcp-server {
        disabled false
        shared-network-name wired-eth0 {
            authoritative disable
            description "Wired Network - Eth1"
            subnet 10.61.4.0/24 {
                default-router 10.61.4.1
                dns-server 10.61.4.1
                lease 86400
                ntp-server 10.61.4.1
                start 10.61.4.50 {
                    stop 10.61.5.254
                }
                time-server 10.61.4.1
            }
        }
        shared-network-name wireless-eth1 {
            authoritative disable
            description "Wireless Network - Eth2"
            subnet 10.61.6.0/24 {
                default-router 10.61.6.1
                dns-server 10.61.6.1
                lease 86400
                ntp-server 10.61.6.1
                start 10.61.6.50 {
                    stop 10.61.7.254
                }
                time-server 10.61.6.1
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
        listen-address 10.61.6.1
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
        listen-address 10.61.6.1
        port 22
        protocol-version v2
    }
    upnp {
        listen-on eth0 {
            outbound-interface pppoe0
        }
        listen-on eth1 {
            outbound-interface pppoe0
        }
    }
}
system {
    domain-name jenni.local
    host-name sentinel
    ipv6 {
        disable
    }
    login {
        user ubnt {
            authentication {
                encrypted-password "$1$zKNoUbAo$gomzUbYvgyUMcD436Wo66."
            }
            level admin
        }
    }
    name-server 8.8.8.8
    name-server 208.67.220.220
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
/* === vyatta-config-version: "ubnt-pptp@1:nat@3:conntrack@1:dhcp-server@4:cron@1:zone-policy@1:quagga@2:vrrp@1:system@4:qos@1:webgui@1:config-management@1:ipsec@4:firewall@5:ubnt-util@1:webproxy@1:dhcp-relay@1" === */
/* Release version: v1.7.0.4783374.150622.1534 */
