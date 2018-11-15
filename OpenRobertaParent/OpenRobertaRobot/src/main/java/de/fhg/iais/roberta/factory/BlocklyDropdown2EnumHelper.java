package de.fhg.iais.roberta.factory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.fhg.iais.roberta.components.Category;
import de.fhg.iais.roberta.inter.mode.general.IMode;
import de.fhg.iais.roberta.syntax.BlockTypeContainer;
import de.fhg.iais.roberta.util.DropDown;
import de.fhg.iais.roberta.util.dbc.DbcException;

public class BlocklyDropdown2EnumHelper {

    public static <E extends IMode> E getModeValue(String modeName, Class<E> modes) {
        if ( modeName == null ) {
            throw new DbcException("Invalid " + modes.getName() + ": " + modeName);
        }
        final String sUpper = modeName.trim().toUpperCase(Locale.GERMAN);
        for ( final E mode : modes.getEnumConstants() ) {
            if ( mode.toString().equals(sUpper) ) {
                return mode;
            }
            for ( final String value : mode.getValues() ) {
                if ( sUpper.equals(value.toUpperCase()) ) {
                    return mode;
                }
            }
        }
        throw new DbcException("Invalid " + modes.getName() + ": " + modeName);
    }

    public static DropDown getColors(JSONObject robotDescription) {
        DropDown dropdownItem = new DropDown();
        JSONObject colors = robotDescription.getJSONObject("color");
        for ( String key : colors.keySet() ) {
            String value = colors.getString(key);
            dropdownItem.add(key, value);
        }
        return dropdownItem;
    }

    public static Map<String, String> getModes(JSONObject robotDescription) {
        Map<String, String> map = new HashMap<>();
        JSONObject modes = robotDescription.getJSONObject("mode");
        for ( String key : modes.keySet() ) {
            JSONArray v = modes.getJSONArray(key);
            for ( int i = 0; i < v.length(); i++ ) {
                String mode = v.getString(i);
                map.put(mode, mode);
            }
        }
        return map;
    }

    public static Map<String, WaitUntilSensorBean> getWaitUntils(JSONObject robotDescription) {
        Map<String, WaitUntilSensorBean> map = new HashMap<>();
        JSONObject waitUntils = robotDescription.getJSONObject("wait");
        for ( String key : waitUntils.keySet() ) {
            JSONObject v = waitUntils.getJSONObject(key);
            map.put(key, new WaitUntilSensorBean(v.getString("implementor"), v.getString("sensor"), v.getString("mode")));
        }
        return map;
    }

    public static DropDown getConfigurationComponents(JSONObject robotDescription) {
        DropDown dropdownItems = new DropDown();
        JSONObject configurations = robotDescription.getJSONObject("configuration");
        for ( String key : configurations.keySet() ) {
            String value = configurations.getString(key);
            dropdownItems.add(key, value);
        }
        return dropdownItems;
    }

    public static void loadBlocks(JSONObject robotDescription) {
        JSONObject blocks = robotDescription.getJSONObject("block");
        for ( String block : blocks.keySet() ) {
            JSONObject value = blocks.getJSONObject(block);

            try {
                Class<?> implementor = Class.forName(value.getString("implementor"));
                JSONArray blocklyBlocks = value.getJSONArray("type");
                String[] blocklyNames = jsonArray2stringArray(blocklyBlocks);
                BlockTypeContainer.add(block, Category.valueOf(value.getString("category")), implementor, blocklyNames);
            } catch ( Exception e ) {
                throw new DbcException("Invalide definition for block type " + block);
            }
        }
    }

    private static String[] jsonArray2stringArray(JSONArray value) {
        int length = value.length();
        String[] resultValues = new String[length];
        for ( int i = 0; i < length; i++ ) {
            resultValues[i] = value.getString(i);
        }
        return resultValues;
    }
}