package me.msicraft.vaulthuntersintegrated.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("vaulthuntersintegrated")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("menu");
                if (sender.isOp()) {
                    arguments.add("help");
                    arguments.add("reload");
                    arguments.add("getbukkittask");
                    arguments.add("setspawn");
                    arguments.add("killpoint");
                }
                return arguments;
            }
            if (sender.isOp()) {
                List<String> arguments = new ArrayList<>();
                if (args.length == 2) {
                    switch (args[0]) {
                        case "killpoint" -> {
                            arguments.add("point");
                            arguments.add("exp");
                            return arguments;
                        }
                    }
                }
                if (args.length == 3) {
                    switch (args[0]) {
                        case "killpoint" -> {
                            if (args[1].equals("point") || args[1].equals("exp")) {
                                arguments.add("get");
                                arguments.add("set");
                                arguments.add("add");
                                return arguments;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
