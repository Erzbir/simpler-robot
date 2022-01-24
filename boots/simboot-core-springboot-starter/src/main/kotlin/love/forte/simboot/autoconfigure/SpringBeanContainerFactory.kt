/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.autoconfigure

import love.forte.di.*
import love.forte.simboot.factory.BeanContainerFactory
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.NoUniqueBeanDefinitionException
import kotlin.reflect.KClass

//region bean container
public open class SpringBeanContainerFactory(private val listableBeanFactory: ListableBeanFactory) :
    BeanContainerFactory {
    override fun invoke(configuration: love.forte.simboot.Configuration): BeanContainer {
        return SpringBeanContainer(listableBeanFactory)
    }
}


public open class SpringBeanContainer(override val listableBeanFactory: ListableBeanFactory) : BeanContainer,
    love.forte.di.spring.SpringBeanContainer {

    //// ———————— container ———————— ////
    override fun contains(name: String): Boolean {
        return listableBeanFactory.containsBean(name)
    }

    override fun get(name: String): Any {
        return try {
            listableBeanFactory.getBean(name)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("named $name", e)
        }
    }


    override fun <T : Any> get(name: String, type: KClass<T>): T {
        return try {
            listableBeanFactory.getBean(name, type.java)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("named $name with type of $type", e)
        }
    }

    override fun <T : Any> getOrNull(name: String, type: KClass<T>): T? {
        return if (name in this) this[name, type] else null
    }

    @Api4J
    override fun <T : Any> get(name: String, type: Class<T>): T {
        return try {
            listableBeanFactory.getBean(name, type)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("named $name with type of $type", e)
        }
    }

    @Api4J
    override fun <T : Any> getOrNull(name: String, type: Class<T>): T? {
        return if (name in this) this[name, type] else null
    }

    override fun <T : Any> get(type: KClass<T>): T {
        return try {
            listableBeanFactory.getBean(type.java)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("type of $type", e)
        } catch (e: NoUniqueBeanDefinitionException) {
            throw MultiSameTypeBeanException(type.toString(), e)
        }
    }

    @Api4J
    override fun <T : Any> get(type: Class<T>): T {
        return try {
            listableBeanFactory.getBean(type)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("type of $type", e)
        } catch (e: NoUniqueBeanDefinitionException) {
            throw MultiSameTypeBeanException(type.toString(), e)
        }
    }

    @Api4J
    override fun <T : Any> getOrNull(type: Class<T>): T? {
        val names = listableBeanFactory.getBeanNamesForType(type)
        return when {
            names.isEmpty() -> null
            names.size == 1 -> this[names[0], type]
            else -> try {
                get(type)
            } catch (e: NoSuchBeanDefinitionException) {
                null
            } catch (e: NoUniqueBeanDefinitionException) {
                throw MultiSameTypeBeanException(type.toString(), e)
            }
        }
    }

    @Api4J
    override fun <T : Any> getAll(type: Class<T>?): List<String> {
        return listableBeanFactory.getBeanNamesForType(type).toList()
    }

    override fun getOrNull(name: String): Any? {
        return if (name in this) this[name] else null
    }

    override fun <T : Any> getOrNull(type: KClass<T>): T? {
        val names = listableBeanFactory.getBeanNamesForType(type.java)
        return when {
            names.isEmpty() -> null
            names.size == 1 -> this[names[0], type]
            else -> try {
                listableBeanFactory.getBean(type.java)
            } catch (e: NoSuchBeanDefinitionException) {
                null
            } catch (e: NoUniqueBeanDefinitionException) {
                throw MultiSameTypeBeanException(type.toString(), e)
            }
        }
    }

    override fun <T : Any> getAll(type: KClass<T>?): List<String> {
        return listableBeanFactory.getBeanNamesForType(type?.java).toList()
    }


    @OptIn(Api4J::class)
    override fun getTypeOrNull(name: String): KClass<*>? = getTypeClassOrNull(name)?.kotlin

    @OptIn(Api4J::class)
    override fun getType(name: String): KClass<*> = getTypeClass(name).kotlin

    @Api4J
    override fun getTypeClassOrNull(name: String): Class<*>? {
        return try {
            listableBeanFactory.getType(name)
        } catch (e: NoSuchBeanDefinitionException) {
            null
        }
    }

    @Api4J
    override fun getTypeClass(name: String): Class<*> {
        return try {
            listableBeanFactory.getType(name) ?: noSuchBeanDefine { name }
        } catch (e: NoSuchBeanDefinitionException) {
            noSuchBeanDefine(e) { name }
        }
    }
}
//endregion
