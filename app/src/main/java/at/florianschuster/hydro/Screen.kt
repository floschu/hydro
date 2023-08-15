package at.florianschuster.hydro

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface Screen : Parcelable {
    @Parcelize
    data object Onboarding : Screen

    @Parcelize
    data object Today : Screen

    @Parcelize
    data object Settings : Screen

    @Parcelize
    data object History : Screen
}

sealed interface Sheet : Parcelable {

    @Parcelize
    data object GoalOfTheDay : Sheet

    @Parcelize
    data object LiquidUnit : Sheet

    @Parcelize
    data object SetTheme : Sheet

    @Parcelize
    data object SetInterval : Sheet
}
