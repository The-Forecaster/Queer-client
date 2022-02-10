package trans.rights.event.bus.impl

import java.lang.reflect.Field
import java.util.Arrays
import java.util.concurrent.CopyOnWriteArraySet
import trans.rights.event.annotation.EventHandler
import trans.rights.event.listener.impl.MethodListener
import trans.rights.event.listener.impl.LambdaListener
import trans.rights.event.listener.Listener
import type.ICancellable
import java.lang.reflect.Method

object BasicEventManager : AbstractEventBus() {
    override fun registerFields(subscriber: Any) {
        Arrays.stream(subscriber.javaClass.declaredFields).filter { field ->
            field.isValid(subscriber)
        }
        .forEach { field ->
            this.subscribers.getOrPut(field.asListener(subscriber).target, ::CopyOnWriteArraySet).add(field.asListener(subscriber))
        }
    }

    override fun registerMethods(subscriber: Any) {
        Arrays.stream(subscriber.javaClass.declaredMethods).filter { method ->
            method.isValid()
        }
        .forEach { method ->
            this.subscribers.getOrPut(method.parameters[0].type, ::CopyOnWriteArraySet).add(method.asListener(subscriber))
        }
    }

    override fun unregisterFields(subscriber: Any) {
        Arrays.stream(subscriber.javaClass.declaredFields).filter { field ->
            field.isValid(subscriber)
        }
        .forEach { field ->
            this.subscribers[field.type]?.remove(field.get(subscriber))
        }
    }

    override fun unregisterMethods(subscriber: Any) {
        Arrays.stream(subscriber.javaClass.declaredMethods).filter { method ->
            method.isValid()
        }
        .forEach { method ->
            this.subscribers[method.parameters[0].type]?.remove(method.asListener(subscriber))
        }
    }

    fun <T: ICancellable> dispatch(event: T): T {
        if (this.subscribers[event::class.java]?.size != 0) {
            for (listener in getOrPutList(event.javaClass)) {
                listener.invoke(event)

                if (event.isCancelled) break
            }
        }

        return event
    }
}

private fun Method.isValid(): Boolean = this.isAnnotationPresent(EventHandler::class.java) && this.parameterCount == 1

private fun Field.isValid(parent: Any): Boolean = this.isAnnotationPresent(EventHandler::class.java) && this.get(parent) is Listener<*>

private fun Method.asListener(parent: Any): MethodListener<*> {
    this.trySetAccessible()

    return MethodListener(this, this.getAnnotation(EventHandler::class.java).priority, parent, this.parameters[0]::class.java)
}

private fun Field.asListener(parent: Any): LambdaListener<*> {
    this.trySetAccessible()

    return this.get(parent) as LambdaListener<*>
}
