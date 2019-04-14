package ru.rpuch.demo.reactivemongo.service;

/**
 * @author rpuch
 */
public enum DeletionStatus {
    DELETED, DID_NOT_EXIST;

    public boolean deleted() {
        return this == DELETED;
    }
}
