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
