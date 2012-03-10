# Configuration

Below is an example `config.yml`. Type `log reload` from the console or `/log reload` in-game to reload the config and apply new logging level filters.

```yaml
default.level: INFO # default logging level, overridden by the levels below
test: false # set to true to test log levels
prefix: false # set to true to prefix all log messages with their logger name

loggers:
  - name: com.minecarts.loggermanager.LoggerManager
    level: ALL

  - name: com.minecarts.dbpermissions.DBPermissions
    level: WARNING

  - name: com.realmdata.metrics.Events
    level: WARNING
    prefix: "RealmDataMetrics> "
```