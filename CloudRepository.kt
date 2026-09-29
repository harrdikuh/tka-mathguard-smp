package com.example.tkamathguard

data class CloudResult(
    val classCode: String,
    val studentName: String,
    val correct: Int,
    val wrong: Int,
    val score: Int,
    val integrity: Int,
    val submittedAt: Long = System.currentTimeMillis()
)

/**
 * Interface separating the UI from the backend.
 * Current implementation is local/demo. A Firebase implementation can
 * implement the same interface without changing the quiz UI.
 */
interface CloudRepository {
    suspend fun submitResult(result: CloudResult): Result<Unit>
    suspend fun getClassResults(classCode: String): Result<List<CloudResult>>
}

class DemoCloudRepository : CloudRepository {
    private val data = mutableListOf<CloudResult>()

    override suspend fun submitResult(result: CloudResult): Result<Unit> {
        data.add(result)
        return Result.success(Unit)
    }

    override suspend fun getClassResults(classCode: String): Result<List<CloudResult>> {
        return Result.success(data.filter { it.classCode == classCode })
    }
}
