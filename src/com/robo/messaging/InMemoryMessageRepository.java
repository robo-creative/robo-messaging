package com.robo.messaging;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides implementation of MessageRepository, stores historic messages in memory.
 *
 * @author robo-admin
 */
public class InMemoryMessageRepository implements MessageRepository {

    private ConcurrentHashMap<Class<?>, List<Message>> mMessages;

    public InMemoryMessageRepository() {
        mMessages = new ConcurrentHashMap<>();
    }

    @Override
    public int size() {
        int size = 0;
        for (List<Message> messages : mMessages.values()) {
            size += messages.size();
        }
        return size;
    }

    @Override
    public void store(Message message) {
        Class<?> messageType = message.getClass();
        mMessages.putIfAbsent(messageType, new ArrayList<Message>());
        List<Message> messages = mMessages.get(messageType);
        synchronized (messages) {
            if (!messages.contains(message)) {
                messages.add(message);
            }
        }
    }

    @Override
    public boolean remove(Message message) {
        Class<?> messageType = message.getClass();
        mMessages.putIfAbsent(messageType, new ArrayList<Message>());
        List<Message> messages = mMessages.get(messageType);
        synchronized (messages) {
            boolean success = messages.remove(message);
            if (success && messages.isEmpty()) {
                mMessages.remove(messageType);
            }
            return success;
        }
    }

    @Override
    public void removeAll() {
        mMessages.clear();
    }

    @Override
    public Collection<Message> find(Class<? extends Message> contractType, boolean includeChildren) {
        Collection<Message> messages = new ArrayList<>();
        List<Message> byContractTypeMessages = mMessages.get(contractType);
        if (null != byContractTypeMessages) {
            messages.addAll(byContractTypeMessages);
        }
        if (includeChildren) {
            for (Map.Entry<Class<?>, List<Message>> entry : mMessages.entrySet()) {
                if (contractType.isAssignableFrom(entry.getKey()) && !entry.getKey().equals(contractType)) {
                    messages.addAll(entry.getValue());
                }
            }
        }
        return messages;
    }
}
