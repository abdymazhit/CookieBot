package net.Abdymazhit.CookieBot.seperateChannels;

/**
 * Отвечает за работу отдельных каналов
 *
 * @version   01.09.2021
 * @author    Islam Abdymazhit
 */
public class SeparateChannels {

    /** Канал авторизации */
    private final AuthChannel authChannel;

    /** Канал верификации тикетов */
    private final VerificationChannel verificationChannel;

    /**
     * Инициализирует отдельные каналы
     */
    public SeparateChannels() {
        authChannel = new AuthChannel();
        verificationChannel = new VerificationChannel();
    }

    /**
     * Получает канал авторизации
     * @return Канал авторизации
     */
    public AuthChannel getAuthChannel() {
        return authChannel;
    }

    /**
     * Получает канал верификации тикетов
     * @return Канал верификации тикетов
     */
    public VerificationChannel getVerificationChannel() {
        return verificationChannel;
    }
}