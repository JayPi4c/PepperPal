package de.jaypi4c.pepperpal.dashboard.service;

import de.jaypi4c.pepperpal.dashboard.model.SoilData;
import org.apache.commons.math4.legacy.analysis.interpolation.LoessInterpolator;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

@Service
public class SoilDataPreparationService {

    /**
     * Fraction of the dataset used for each local regression.
     * <p>
     * Smaller:
     * more detail
     * less smoothing
     * <p>
     * Larger:
     * smoother curve
     */
    private static final double BANDWIDTH = 0.08;

    /**
     * Robustness iterations reduce the influence of remaining outliers.
     */
    private static final int ROBUSTNESS_ITERATIONS = 2;

    /**
     * Hampel filter settings.
     */
    private static final int HAMPEL_WINDOW = 5;
    private static final double HAMPEL_THRESHOLD = 3.0;

    private final LoessInterpolator loess = new LoessInterpolator(BANDWIDTH, ROBUSTNESS_ITERATIONS);

    public List<SoilData> prepare(List<SoilData> input) {

        if (input.size() < 5) {
            return input;
        }

        List<SoilData> sorted = input.stream()
                .sorted(Comparator.comparing(SoilData::getCreated))
                .toList();

        double[] x = createTimeAxis(sorted);

        double[] temperatures = smooth(x, sorted, SoilData::getTemperature);
        double[] humidities = smooth(x, sorted, SoilData::getRelativeHumidity);
        double[] moisture = smooth(x, sorted, SoilData::getMoistureLevel);

        List<SoilData> result = new ArrayList<>(sorted.size());

        for (int i = 0; i < sorted.size(); i++) {

            SoilData original = sorted.get(i);

            SoilData copy = new SoilData();

            copy.setId(original.getId());
            copy.setCreated(original.getCreated());
            copy.setUpdated(original.getUpdated());

            copy.setTemperature((float) round(temperatures[i]));
            copy.setRelativeHumidity((float) round(humidities[i]));
            copy.setMoistureLevel((float) round(moisture[i]));

            result.add(copy);
        }

        return result;
    }

    private double[] smooth(double[] x, List<SoilData> data, ToDoubleFunction<SoilData> extractor) {
        double[] values = data.stream()
                .mapToDouble(extractor)
                .toArray();
        hampelFilter(values);
        return loess.smooth(x, values);
    }

    private double[] createTimeAxis(List<SoilData> data) {
        double[] x = new double[data.size()];
        long first = data.getFirst()
                .getCreated()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        for (int i = 0; i < data.size(); i++) {
            x[i] = (data.get(i)
                    .getCreated()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli() - first) / 1000.0;
        }

        return x;
    }

    /**
     * Replaces obvious outliers by the local median.
     */
    private void hampelFilter(double[] values) {
        int radius = HAMPEL_WINDOW / 2;
        double[] copy = values.clone();
        for (int i = radius; i < values.length - radius; i++) {
            double[] window = new double[HAMPEL_WINDOW];
            System.arraycopy(copy, i - radius, window, 0, HAMPEL_WINDOW);
            Arrays.sort(window);
            double median = window[radius];
            double[] deviations = new double[HAMPEL_WINDOW];
            for (int j = 0; j < HAMPEL_WINDOW; j++) {
                deviations[j] = Math.abs(window[j] - median);
            }
            Arrays.sort(deviations);
            double mad = deviations[radius];
            if (mad == 0.0) {
                continue;
            }
            double score = Math.abs(copy[i] - median) / (1.4826 * mad);
            if (score > HAMPEL_THRESHOLD) {
                values[i] = median;
            }
        }
    }

    /**
     * Keeps values looking nice in the UI.
     */
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}