package at.florianschuster.hydro.model

enum class LiquidUnit(
    val baseUnitMultiplier: Double,
    val serialized: String
) {
    Milliliter(
        baseUnitMultiplier = 1.0,
        serialized = "ml"
    ),
    USFluidOunce(
        baseUnitMultiplier = 0.033814, // 1 milliliter = 0.033814 oz
        serialized = "oz_us"
    ),
    UKFluidOunce(
        baseUnitMultiplier = 0.035195, // 1 milliliter = 0.035195 oz
        serialized = "oz_uk"
    );

    fun convertValue(
        milliliters: Milliliters
    ): Double = milliliters.value * baseUnitMultiplier

    companion object {
        fun of(serialized: String?): LiquidUnit = if (serialized == null) {
            Milliliter
        } else {
            entries.firstOrNull { it.serialized == serialized } ?: Milliliter
        }
    }
}

fun LiquidUnit.format() = when (this) {
    LiquidUnit.Milliliter -> "Milliliters (ml)"
    LiquidUnit.USFluidOunce -> "US Ounces (fl oz US)"
    LiquidUnit.UKFluidOunce -> "UK Ounces (fl oz UK)"
}

fun LiquidUnit.formatMoreInfo() = when (this) {
    LiquidUnit.Milliliter ->
        "Milliliters are a standard metric unit of liquid volume. " +
            "They provide a precise way to measure fluids, making them ideal for accurate " +
            "hydration monitoring."

    LiquidUnit.USFluidOunce ->
        "US ounces serve as a customary unit for liquid volume " +
            "predominantly in the United States."

    LiquidUnit.UKFluidOunce ->
        "UK ounces, also referred to as Imperial ounces, are a " +
            "volume measurement commonly utilized in the United Kingdom and certain other " +
            "regions."
}
