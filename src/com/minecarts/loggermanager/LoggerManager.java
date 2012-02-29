package com.minecarts.loggermanager;

import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.text.MessageFormat;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;


public class LoggerManager extends JavaPlugin {
    protected Level defaultLevel;
    protected Map<String, Level> levels = new HashMap<String, Level>();
    
    @Override
    public void onEnable() {        
        reloadConfig();
        
        // internal plugin commands
        getCommand("loggermanager").setExecutor(new CommandExecutor() {
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if(!sender.hasPermission("loggermanager.reload")) return true; // "hide" command output for nonpermissibles
                
                if(args[0].equalsIgnoreCase("reload")) {
                    LoggerManager.this.reloadConfig();
                    sender.sendMessage("LoggerManager config reloaded.");
                    log("Config reloaded by {0}", sender.getName());
                    return true;
                }
                
                return false;
            }
        });
        
        log("Version {0} enabled.", getDescription().getVersion());
    }
    
    
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        final FileConfiguration config = getConfig();
        
        defaultLevel = getLevel(config.getString("default.level"), Level.INFO);
        log("Default level set to {0}", defaultLevel);
        
        for(Handler handler : Logger.getLogger("").getHandlers()) {
            if(handler instanceof ConsoleHandler) {
                handler.setFilter(new Filter() {
                    public boolean isLoggable(LogRecord log) {
                        Level level = levels.get(log.getLoggerName());
                        return log.getLevel().intValue() >= (level == null ? defaultLevel : level).intValue();
                    }
                });
                handler.setLevel(Level.ALL);
            }
        }
        
        levels.clear();
        // TODO: revert to getMapList once null check is added to Bukkit
        List<Object> loggerSettings = config.getList("loggers");
        if(loggerSettings != null) {
            for(Object settings : loggerSettings) {
                if(settings instanceof Map) {
                    Object logger = ((Map<String, Object>) settings).get("name");
                    if(logger == null || !(logger instanceof String)) continue;
                    
                    Level level = getLevel((String) ((Map<String, Object>) settings).get("level"));
                    if(level != null) {
                        levels.put((String) logger, level);
                        log("Logger \"{0}\" level set to {1}", logger, level);
                    }
                }
            }
        }
        
        if(config.getBoolean("test")) {
            log(Level.SEVERE, "Test message of level SEVERE");
            log(Level.WARNING, "Test message of level WARNING");
            log(Level.INFO, "Test message of level INFO");
            log(Level.CONFIG, "Test message of level CONFIG");
            log(Level.FINE, "Test message of level FINE");
            log(Level.FINER, "Test message of level FINER");
            log(Level.FINEST, "Test message of level FINEST");
        }
    }
    
    
    public static Level getLevel(String name) {
        return getLevel(name, null);
    }
    public static Level getLevel(String name, Level defaultLevel) {
        try {
            Level level = Level.parse(name);
            return level == null ? defaultLevel : level;
        }
        catch(Exception e) {
            return defaultLevel;
        }
    }
    
    
    public void log(String message) {
        log(Level.INFO, message);
    }
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }
    public void log(String message, Object... args) {
        log(MessageFormat.format(message, args));
    }
    public void log(Level level, String message, Object... args) {
        log(level, MessageFormat.format(message, args));
    }
    
    public void debug(String message) {
        log(Level.FINE, message);
    }
    public void debug(String message, Object... args) {
        log(Level.FINE, message, args);
    }
}