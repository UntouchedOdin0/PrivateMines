package me.untouchedodin0.privatemines.configmanager.conversion;

import me.untouchedodin0.privatemines.configmanager.ConfigField;
import me.untouchedodin0.privatemines.configmanager.ConversionManager;
import me.untouchedodin0.privatemines.configmanager.data.DataHolder;
import me.untouchedodin0.privatemines.configmanager.instantiation.FieldSummary;

/**
 * A converter which saves to and loads from static fields and can only be used as a config target
 * @author Redempt
 */
public class StaticRootConverter {

    /**
     * Creates a static root converter
     * @param manager The ConfigManager handling the data
     * @param root The class to save and load from non-transient static fields of
     * @param <T> The type
     * @return A static root converter
     */
    public static <T> TypeConverter<T> create(ConversionManager manager, Class<?> root) {
        FieldSummary summary = FieldSummary.getFieldSummary(manager, root, true);
        return new TypeConverter<T>() {
            @Override
            public T loadFrom(DataHolder section, String path, T currentValue) {
                for (ConfigField field : summary.getFields()) {
                    Object val = summary.getConverters().get(field).loadFrom(section, field.getName(), null);
                    field.set(val);
                }
                return null;
            }

            @Override
            public void saveTo(T t, DataHolder section, String path) {
                saveTo(t, section, path, true);
            }

            @Override
            public void saveTo(T t, DataHolder section, String path, boolean overwrite) {
                for (ConfigField field : summary.getFields()) {
                    Object obj = field.get();
                    saveWith(summary.getConverters().get(field), obj, section, field.getName(), overwrite);
                }
                summary.applyComments(section);
            }
        };
    }

    private static <T> void saveWith(TypeConverter<T> converter, Object obj, DataHolder section, String path, boolean overwrite) {
        converter.saveTo((T) obj, section, path, overwrite);
    }

}