package net.adambruce.dsn.now.model.state;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.Setter;

import java.time.Duration;

/**
 * DSN spacecraft (target) information.
 */
@Data
public class Target {
    /**
     * the name of the spacecraft
     * @param name the name of the spacecraft
     * @return the name of the spacecraft
     */
    private String name;

    /**
     * the ID of the target
     * @param id the ID of the target
     * @return the ID of the target
     */
    private Long id;

    /**
     * the up leg range (m)
     * @param upLegRange the up leg range (m)
     * @return the up leg range (m)
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "uplegRange"))
    private Long upLegRange;

    /**
     * the down leg range (m)
     * @param downLegRange the down leg range (m)
     * @return the down leg range (m)
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "downlegRange"))
    private Long downLegRange;

    /**
     * the Round Trip Light Time (RTLT) (s)
     * @param roundTripLightTime the RTLT (s)
     * @return the RTLT (s)
     */
    @Setter(onMethod_ = @JacksonXmlProperty(localName = "rtlt"))
    private Duration roundTripLightTime;
}
