package com.minecarts.loggermanager;

import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.MessageFormat;

import java.util.Map;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;


public class LoggerManager extends JavaPlugin {
    private final static Logger topLogger = Logger.getLogger("");
    
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
        
        String defaultLevel = config.getString("default.level");
        if(defaultLevel != null) {
            for(Handler handler : topLogger.getHandlers()) {
                if(handler instanceof ConsoleHandler) {
                    setLevel(handler, defaultLevel);
                    log(Level.CONFIG, "ConsoleHandler {0} level set to {1}", handler, defaultLevel);
                }
            }
        }
        
        // TODO: revert to getMapList once null check is added to Bukkit
        List<Object> loggerSettings = config.getList("loggers");
        if(loggerSettings != null) {
            for(Object settings : loggerSettings) {
                if(settings instanceof Map) {
                    Object name = ((Map<String, Object>) settings).get("name");
                    if(name == null || !(name instanceof String)) continue;

                    Logger logger = Logger.getLogger((String) name);
                    
                    Object level = ((Map<String, Object>) settings).get("level");
                    if(level != null && level instanceof String) setLevel(logger, (String) level);
                    log(Level.CONFIG, "Logger \"{0}\" level set to {1}", logger.getName(), level);
                }
            }
        }
    }
    
    
    private void setLevel(Logger logger, String level) {
        try {
            logger.setLevel(Level.parse(level));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void setLevel(Handler logger, String level) {
        try {
            logger.setLevel(Level.parse(level));
        }
        catch(Exception e) {
            e.printStackTrace();
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