package lorry.lebonfilm._tools

import lorry.lebonfilm._components.displayActor.DisplayedActor
import lorry.lebonfilm.ui.screens.Screens
import lorry.lebonfilm._repos.APIRepo
import lorry.lebonfilm._repos.ActorFilmsManager
import lorry.lebonfilm._repos.ActorManager
import lorry.lebonfilm.ui.screens.SearchActor
import lorry.lebonfilm.ui.screens.Tools
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::Logger)
    singleOf(::Tools)
    singleOf(::APIRepo)
    singleOf(::ActorManager)
    singleOf(::ActorFilmsManager)
    singleOf(::DisplayedActor)
    singleOf(Screens::NavigationState)
    singleOf(::SearchActor)
}