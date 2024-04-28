package au.com.cding21.services

import au.com.cding21.model.Transaction
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId
import org.litote.kmongo.*


class TransactionService (
    private val db: MongoDatabase
) {
    private var transactions: MongoCollection<Document>

    init {
        db.createCollection("transactions")
        transactions = db.getCollection("transactions")
    }

    suspend fun createTransaction(transaction: Transaction): String = withContext(Dispatchers.IO) {
        val doc = transaction.toDocument()
        transactions.insertOne(doc)
        doc["_id"].toString()
    }

    suspend fun getTransactionByUserId(userId: String): List<Transaction> = withContext(Dispatchers.IO) {
        transactions.find(Filters.eq("userId", userId)).toList().map { doc ->
            Transaction.fromDocument(doc)
        }
    }

    suspend fun getTransactionById(id: String): Transaction? = withContext(Dispatchers.IO) {
        transactions.findOneById(Filters.eq("_id", ObjectId(id)))?.let { doc ->
            Transaction.fromDocument(doc)
        }
    }

    suspend fun updateTransaction(id: String, transaction: Transaction): Document? = withContext(Dispatchers.IO) {
        transactions.findOneAndReplace(Filters.eq("_id", ObjectId(id)), transaction.toDocument())
    }

    suspend fun deleteTransaction(id: String): Document? = withContext(Dispatchers.IO) {
        transactions.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}






