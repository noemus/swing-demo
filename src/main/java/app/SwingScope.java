package app;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContextException;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class SwingScope implements Scope {
    public static final String SWING_SCOPE = "swing-scope";

    private final ConcurrentMap<String,Object> instances = new ConcurrentHashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return instances.computeIfAbsent(name, key -> createInstance(objectFactory));
    }

    private Object createInstance(ObjectFactory<?> objectFactory) {
        if (SwingUtilities.isEventDispatchThread()) {
            return objectFactory.getObject();
        }
        try {
            AtomicReference<Object> ref = new AtomicReference<>();
            SwingUtilities.invokeAndWait(() -> ref.set(objectFactory.getObject()));
            return ref.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApplicationContextException("Bean creation was interrupted", e);
        } catch (InvocationTargetException e) {
            throw new BeanCreationException("Scoped instanctiation failed", e.getTargetException());
        }
    }

    @Override
    public Object remove(String name) {
        return instances.remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable runnable) {
        // do nothing
    }

    @Override
    public Object resolveContextualObject(String name) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}
