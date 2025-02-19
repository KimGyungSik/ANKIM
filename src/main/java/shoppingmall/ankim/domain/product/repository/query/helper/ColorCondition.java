package shoppingmall.ankim.domain.product.repository.query.helper;

public enum ColorCondition {
    // Define color name and hex code
    BLACK("#000000"),
    GRAY("#808080"),
    BROWN("#A52A2A"),
    WHITE("#FFFFFF"),
    BEIGE("#F5F5DC"),
    BLUE("#0000FF"),
    NAVY("#000080"),
    IVORY("#FFFFF0"),
    SKYBLUE("#87CEEB"),
    PINK("#FFC0CB"),
    GREEN("#008000"),
    LIGHTGRAY("#D3D3D3"),
    YELLOW("#FFFF00"),
    CHARCOAL("#36454F"),
    KHAKI("#BDB76B"),
    RED("#FF0000"),
    PURPLE("#800080"),
    MINT("#3EB489"),
    ORANGE("#FFA500"),
    BURGUNDY("#800020"),
    TAN("#D2B48C"),
    BABYPINK("#F4C2C2"),
    LAVENDER("#E6E6FA"),
    CAMEL("#C19A6B");

    private final String hexCode;

    ColorCondition(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getHexCode() {
        return hexCode;
    }
    public static String findHexCodeByName(String name) {
        return java.util.Arrays.stream(values())
                .filter(color -> color.name().equalsIgnoreCase(name))
                .map(ColorCondition::getHexCode)
                .findFirst()
                .orElse(null);
    }
}

