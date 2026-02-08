package net.adambruce.dsn.now.model.state;

/**
 * DSN signal information.
 *
 * @param active the signal is active
 * @param signalType the type of the signal
 * @param dataRate the rate of data transmitted / received
 * @param frequency the frequency of the signal (Hz)
 * @param band the band of the signal
 * @param power the power of the signal (dBm)
 * @param spacecraft the spacecraft this signal is communicating with
 * @param spacecraftId the ID of the spacecraft this signal is communicating with
 */
public record Signal(
        Boolean active,
        String signalType,
        Long dataRate,
        Long frequency,
        String band,
        Double power,
        String spacecraft,
        Long spacecraftId
) { }
