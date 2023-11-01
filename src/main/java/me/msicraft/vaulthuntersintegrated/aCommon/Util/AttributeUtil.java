package me.msicraft.vaulthuntersintegrated.aCommon.Util;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;


public class AttributeUtil {

    public static double getMaxHealth(LivingEntity livingEntity) {
        double value = 0;
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance != null) {
            value = instance.getValue();
        }
        return value;
    }

    public static double getDamageValue(LivingEntity livingEntity) {
        double value = 0;
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            value = instance.getValue();
        }
        return value;
    }

    public static double getAttackSpeedValue(LivingEntity livingEntity) {
        double value = 0;
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance != null) {
            value = instance.getValue();
        }
        return value;
    }


    public static double getArmorValue(LivingEntity livingEntity) {
        double value = 0;
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ARMOR);
        if (instance != null) {
            value = instance.getValue();
        }
        return value;
    }

    public static double getArmorToughnessValue(LivingEntity livingEntity) {
        double value = 0;
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        if (instance != null) {
            value = instance.getValue();
        }
        return value;
    }

}
