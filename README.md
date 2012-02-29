# Configuration

Below is an example `config.yml`. Type `log reload` from the console or `/log reload` in-game to reload the config and apply new logging level filters.

```yaml
default.level: INFO # default logging level
test: false # set to true to test log levels

loggers:
  - name: com.minecarts.loggermanager.LoggerManager
    level: ALL

  - name: com.minecarts.dbpermissions.DBPermissions
    level: WARNING
```