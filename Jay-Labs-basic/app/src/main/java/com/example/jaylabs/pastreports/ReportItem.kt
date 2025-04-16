package com.example.jaylabs.pastreports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jaylabs.models.Diagnosis
import com.example.jaylabs.models.ModelResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportItem(
    val id: String = "",
    val interpretation: String = "",
    val sex: String = "",
    val age: String = "",
    val anatomSite: String = "",
    val melanomaProbability: Double = 0.0,
    val nevusProbability: Double = 0.0,
    val timestamp: Long = 0
)

@HiltViewModel
class PastReportsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _reports = MutableStateFlow<List<ReportItem>>(emptyList())
    val reports = _reports.asStateFlow()

    init {
        fetchReports()
    }

    private fun fetchReports() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .collection("reports")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                val fetchedReports = result.documents.mapNotNull { doc ->
                    doc.toObject(ReportItem::class.java)?.copy(id = doc.id)
                }
                _reports.value = fetchedReports
            }
    }
}
// Extension function to convert ReportItem to ModelResponse
fun ReportItem.toModelResponse(): ModelResponse {
    return ModelResponse(
        diagnosis = Diagnosis(
            Melanoma = this.melanomaProbability,
            Nevus = this.nevusProbability
        ),
        interpretation = this.interpretation
    )
}
