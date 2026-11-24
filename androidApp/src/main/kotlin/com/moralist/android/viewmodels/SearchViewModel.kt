/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moralist.shared.viewmodels.SearchViewModel as SharedSearchViewModel

class SearchViewModel : ViewModel() {
    val shared = SharedSearchViewModel(viewModelScope)
    var hasRequestedInitialFocus = false
}
