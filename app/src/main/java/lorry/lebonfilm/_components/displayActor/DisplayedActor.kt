package lorry.lebonfilm._components.displayActor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import lorry.lebonfilm._repos.CommonResponseBuilders

class DisplayedActor {
    var actor by mutableStateOf<CommonResponseBuilders.Actor?>(null)
}
