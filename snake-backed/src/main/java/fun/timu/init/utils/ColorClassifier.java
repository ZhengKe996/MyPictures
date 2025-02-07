package fun.timu.init.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ColorClassifier {
    // 使用LinkedHashMap保持颜色系统的插入顺序
    private static final LinkedHashMap<ColorSystem, String> COLOR_SYSTEMS = new LinkedHashMap<>();

    // 使用枚举定义色系
    public enum ColorSystem {
        // 红色系
        BRIGHT_RED("鲜红", "0xFF0000"), DARK_RED("暗红", "0x8B0000"), PINK("粉红", "0xFFC0CB"), CORAL("珊瑚红", "0xFF7F50"),

        // 橙色系
        ORANGE("橙色", "0xFFA500"), DARK_ORANGE("深橙", "0xFF8C00"), PEACH("杏色", "0xFFE4B5"),

        // 黄色系
        YELLOW("鲜黄", "0xFFFF00"), GOLDEN("金黄", "0xFFD700"), LIGHT_YELLOW("浅黄", "0xFFFFE0"),

        // 绿色系
        GREEN("纯绿", "0x008000"), LAWN_GREEN("草绿", "0x90EE90"), OLIVE("橄榄绿", "0x556B2F"), SPRING_GREEN("青绿", "0x98FF98"),

        // 蓝色系
        BLUE("纯蓝", "0x0000FF"), SKY_BLUE("天蓝", "0x87CEEB"), DARK_BLUE("深蓝", "0x00008B"), ROYAL_BLUE("湖蓝", "0x4169E1"),

        // 紫色系
        PURPLE("纯紫", "0x800080"), LAVENDER("淡紫", "0xE6E6FA"), INDIGO("深紫", "0x4B0082"),

        // 棕色系
        BROWN("棕色", "0x8B4513"), COFFEE("咖啡", "0xD2691E"), SIENNA("褐色", "0xA0522D"),

        // 灰色系
        DARK_GRAY("深灰", "0x696969"), GRAY("中灰", "0x808080"), LIGHT_GRAY("浅灰", "0xD3D3D3"),

        // 黑白系
        BLACK("纯黑", "0x000000"), WHITE("纯白", "0xFFFFFF");

        private final String displayName;
        private final String hexColor;

        ColorSystem(String displayName, String hexColor) {
            this.displayName = displayName;
            this.hexColor = hexColor;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getHexColor() {
            return hexColor;
        }
    }

    static {
        // 初始化色系映射
        for (ColorSystem system : ColorSystem.values()) {
            COLOR_SYSTEMS.put(system, system.getHexColor());
        }
    }

    private static class RGB {
        int r, g, b;

        RGB(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        static RGB fromHex(String hex) {
            hex = hex.replace("0x", "").replace("#", "");
            int rgb = Integer.parseInt(hex, 16);
            return new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
        }

        double getDistance(RGB other) {
            // 使用加权欧几里得距离
            double dr = (this.r - other.r) * 0.3;
            double dg = (this.g - other.g) * 0.59;
            double db = (this.b - other.b) * 0.11;
            return Math.sqrt(dr * dr + dg * dg + db * db);
        }
    }

    /**
     * 获取最接近的色系代表色
     *
     * @param inputColor 输入的颜色值 (0x或#开头的16进制颜色)
     * @return 最接近的色系代表色 (0x格式)
     */
    public static String getClosestSystemColor(String inputColor) {
        if (inputColor.length() == 7) {
            inputColor = inputColor.substring(0, 4) + "0" + inputColor.substring(4, 7);
        }
        RGB inputRGB = RGB.fromHex(inputColor);
        ColorSystem closestSystem = null;
        double minDistance = Double.MAX_VALUE;

        for (ColorSystem system : COLOR_SYSTEMS.keySet()) {
            RGB systemRGB = RGB.fromHex(system.getHexColor());
            double distance = inputRGB.getDistance(systemRGB);

            if (distance < minDistance) {
                minDistance = distance;
                closestSystem = system;
            }
        }

        return closestSystem != null ? closestSystem.getHexColor() : "0x000000";
    }

    /**
     * 获取色系的完整信息
     *
     * @param inputColor 输入的颜色值 (0x或#开头的16进制颜色)
     * @return 色系枚举值
     */
    public static ColorSystem getColorSystem(String inputColor) {
        RGB inputRGB = RGB.fromHex(inputColor);
        ColorSystem closestSystem = null;
        double minDistance = Double.MAX_VALUE;

        for (ColorSystem system : COLOR_SYSTEMS.keySet()) {
            RGB systemRGB = RGB.fromHex(system.getHexColor());
            double distance = inputRGB.getDistance(systemRGB);

            if (distance < minDistance) {
                minDistance = distance;
                closestSystem = system;
            }
        }

        return closestSystem;
    }

    public static void main(String[] args) {
        // 测试示例
        String testColor = "0xFF5733";
        String closestColor = getClosestSystemColor(testColor);
        ColorSystem colorSystem = getColorSystem(testColor);

        System.out.println("输入颜色: " + testColor);
        System.out.println("最接近的色系: " + colorSystem.getDisplayName());
        System.out.println("色系代表色: " + closestColor);
    }
}