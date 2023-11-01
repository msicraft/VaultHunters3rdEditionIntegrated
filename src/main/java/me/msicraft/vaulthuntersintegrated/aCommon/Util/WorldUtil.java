package me.msicraft.vaulthuntersintegrated.aCommon.Util;

import org.bukkit.World;

public class WorldUtil {

    public static String getWorldTimeTo24Format(long gameTime) {
        long hours = gameTime / 1000 + 6;
        long minutes = (gameTime % 1000) * 60 / 1000;
        String ampm = "AM";
        if (hours >= 12) {
            hours -= 12;
            ampm = "PM";
        }
        if (hours >= 12) {
            hours -= 12;
            ampm = "AM";
        }
        if (hours == 0) hours = 12;
        String mm = "0" + minutes;
        mm = mm.substring(mm.length() - 2, mm.length());
        return hours + ":" + mm + " " + ampm;
    }

    public static String getWorldName(World world, boolean isUseNamespaceKey) {
        String worldName = world.getName();
        if (isUseNamespaceKey) {
            if (world.getKey().getNamespace().equals("minecraft")) {
                String namespace = world.getKey().getKey();
                if (namespace.equals("overworld")) {
                    return namespace;
                }
                if (namespace.equals("the_nether")) {
                    return "Nether";
                }
                if (namespace.equals("the_end")) {
                    return "End";
                }
            }
            if (world.getKey().getNamespace().equals("the_vault")) {
                return "Vault";
            }
        } else {
            if (worldName.equals("DIM-1") || worldName.equals("world_the_nether")) {
                return "Nether";
            }
            if (worldName.equals("DIM1") || worldName.equals("world_the_end")) {
                return "End";
            }
        }
        return worldName;
    }

    public static boolean isDay(World world) {
        boolean check = false;
        long time = world.getTime();
        if (time > 1000 && time < 13000) {
            check = true;
        }
        return check;
    }

    public static boolean isNight(World world) {
        return !isDay(world);
    }

}
