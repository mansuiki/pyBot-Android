package com.mansuiki.pybot;

public interface Observable {
    void addObserver(Observer o);

    void delObserver(Observer o);
}
