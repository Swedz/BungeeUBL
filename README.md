# BungeeUBL
Bungeecord Side AutoUBL created by Swedz

## How to use
Simply drag and drop the plugin into your plugins directory on your bungeecord server. Once installed and the server has been run once, it will create an empty configuration file.

In this configuration file, create something like this:
```
servers:
  - uhc1
  - uhc2
  - uhc3
interval: 60
```
This will make the plugin take effect on the servers "uhc1", "uhc2" and "uhc3". It will also automatically update the UBL list every 60 minutes (1 hour).

## Dependencies
This project requires **Java 8+**.
- Apache Commons
- OkIO
- OkHTTP3
