package br.com.vieirateam.tcc.database

import br.com.vieirateam.tcc.model.*

object DatabaseService {

    fun insertShopping(shopping: Shopping) {
        val dao = DatabaseManager.getShoppingDAO()
        dao.insert(shopping)
    }

    fun deleteShopping(shopping: Shopping) {
        val dao = DatabaseManager.getShoppingDAO()
        return dao.delete(shopping)
    }

    fun updateShopping(shopping: Shopping) {
        val dao = DatabaseManager.getShoppingDAO()
        return dao.update(shopping)
    }

    fun selectShopping(user_token: String, shopping_id: Long): Shopping {
        val dao = DatabaseManager.getShoppingDAO()
        return dao.select(user_token, shopping_id)
    }

    fun selectShoppingId(): Long {
        val dao = DatabaseManager.getShoppingDAO()
        return dao.selectId()
    }

    fun selectShoppingList(user_token: String): List<Shopping> {
        val dao = DatabaseManager.getShoppingDAO()
        return dao.select(user_token)
    }

    fun selectShoppingName(name: String): Boolean {
        val dao = DatabaseManager.getShoppingDAO()
        return (dao.selectName(name))
    }

    fun insertShoppingItem(shoppingItem: ShoppingItem) {
        val dao = DatabaseManager.getShoppingItemDAO()
        dao.insert(shoppingItem)
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        val dao = DatabaseManager.getShoppingItemDAO()
        return dao.delete(shoppingItem)
    }

    fun updateShoppingItem(shoppingItem: ShoppingItem) {
        val dao = DatabaseManager.getShoppingItemDAO()
        return dao.update(shoppingItem)
    }

    fun selectShoppingItemList(shopping_id: Long): List<ShoppingItem> {
        val dao = DatabaseManager.getShoppingItemDAO()
        return dao.select(shopping_id)
    }

    fun selectShoppingItem(shopping_id: Long, product_id: Long): ShoppingItem {
        val dao = DatabaseManager.getShoppingItemDAO()
        return dao.select(shopping_id, product_id)
    }

    fun insertStoreList(stores: List<Store>) {
        val dao = DatabaseManager.getStoreDAO()
        for (store in stores) {
            dao.insert(store)
        }
    }

    fun deleteStores() {
        val dao = DatabaseManager.getStoreDAO()
        dao.delete()
    }

    fun selectStoreList(): List<Store> {
        val dao = DatabaseManager.getStoreDAO()
        return dao.select()
    }

    fun insertOfficeHour(officeHour: OfficeHour) {
        val dao = DatabaseManager.getOfficeHourDAO()
        dao.insert(officeHour)
    }

    fun deleteOfficeHours() {
        val dao = DatabaseManager.getOfficeHourDAO()
        dao.delete()
    }

    fun selectOfficeHourList(store_id: Long): List<OfficeHour> {
        val dao = DatabaseManager.getOfficeHourDAO()
        return dao.select(store_id)
    }

    fun insertEntertainmentList(entertainments: List<Entertainment>) {
        val dao = DatabaseManager.getEntertainmentDAO()
        for (entertainment in entertainments) {
            dao.insert(entertainment)
        }
    }

    fun deleteEntertainments() {
        val dao = DatabaseManager.getEntertainmentDAO()
        dao.delete()
    }

    fun selectEntertainmentList(): List<Entertainment> {
        val dao = DatabaseManager.getEntertainmentDAO()
        return dao.select()
    }

    fun insertNotification(notification: Notification) {
        val dao = DatabaseManager.getNotificationDAO()
        dao.insert(notification)
    }

    fun updateNotification(notification: Notification) {
        val dao = DatabaseManager.getNotificationDAO()
        dao.update(notification)
    }

    fun deleteNotification(notification: Notification) {
        val dao = DatabaseManager.getNotificationDAO()
        dao.delete(notification)
    }

    fun selectNotificationList(user_token: String): List<Notification> {
        val dao = DatabaseManager.getNotificationDAO()
        return dao.select(user_token)
    }

    fun selectNotificationCount(user_token: String): Int {
        val dao = DatabaseManager.getNotificationDAO()
        return dao.selectCount(user_token)
    }

    fun selectNotificationLastId(user_token: String): Long {
        val dao = DatabaseManager.getNotificationDAO()
        return dao.selectLastId(user_token)
    }

    fun insertCategoryList(categories: List<Category>) {
        val dao = DatabaseManager.getCategoryDAO()
        for (category in categories) {
            dao.insert(category)
        }
    }

    fun deleteCategories() {
        val dao = DatabaseManager.getCategoryDAO()
        dao.delete()
    }

    fun selectCategoryList(): List<Category> {
        val dao = DatabaseManager.getCategoryDAO()
        return dao.select()
    }

    fun insertUser(user: User) {
        val dao = DatabaseManager.getUserDAO()
        dao.insert(user)
    }

    fun deleteUser(user: User) {
        val dao = DatabaseManager.getUserDAO()
        dao.delete(user)
    }

    fun selectToken(user_token: String): Boolean {
        val dao = DatabaseManager.getUserDAO()
        return dao.selectToken(user_token)
    }

    fun selectUser(user_token: String): User {
        val dao = DatabaseManager.getUserDAO()
        return dao.select(user_token)
    }
}