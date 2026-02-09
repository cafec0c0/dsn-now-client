package net.adambruce.dsn.now.model.state;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Setter;

/**
 * DSN signal information.
 */
@Data
public class Signal {
    /**
     * whether the signal is active
     * @param active whether the signal is active
     * @return whether the signal is active
     */
    private Boolean active;

    /**
     * the signal type
     * @param signalType the signal type
     * @return the signal type
     */
    private String signalType;

    /**
     * the rate of data transmitted / received
     * @param dataRate the rate of data transmitted / received
     * @return the rate of data transmitted / received
     */
    private Long dataRate;

    /**
     * the frequency of the signal (Hz)
     * @param frequency the frequency of the signal (Hz)
     * @return the frequency of the signal (Hz)
     */
    private Long frequency;

    /**
     * the band of the signal
     * @param band the band of the signal
     * @return the band of the signal
     */
    private String band;

    /**
     * the power of the signal (dBm)
     * @param power the power of the signal (dBm)
     * @return the power of the signal (dBm)
     */
    private Double power;

    /**
     * the spacecraft this signal is communicating with
     * @param spacecraft the spacecraft this signal is communicating with
     * @return the spacecraft this signal is communicating with
     */
    private String spacecraft;

    /**
     * the ID of the spacecraft this signal is communicating with
     * @param spacecraftId the ID of the spacecraft this signal is communicating with
     * @return the ID of the spacecraft this signal is communicating with
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "spacecraftID"))
    private Long spacecraftId;
}
