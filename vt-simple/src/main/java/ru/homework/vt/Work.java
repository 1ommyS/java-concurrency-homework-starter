package ru.homework.vt;

/**
 * Интерфейс "блокирующей" работы. В реальной жизни это может быть HTTP/DB/файл и т.п.
 */
public interface Work {
    String doWork(String id) throws Exception;
}
