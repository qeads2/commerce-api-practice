package com.example

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class TearDownDB(
    private val entityManager: EntityManager,
) : InitializingBean {
    private val tableList: MutableList<String> = mutableListOf()

    override fun afterPropertiesSet() {
        val entities = entityManager.metamodel.entities
        tableList.addAll(entities.map { it.name.replace("Entity", "").lowercase() })
    }

    @Transactional
    fun execute() {
        entityManager.flush()
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()
        tableList.forEach {
            entityManager.createNativeQuery("TRUNCATE TABLE $it").executeUpdate()
            entityManager.createNativeQuery("ALTER TABLE " + it + " ALTER COLUMN ID RESTART WITH 1").executeUpdate()
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()
    }
}
