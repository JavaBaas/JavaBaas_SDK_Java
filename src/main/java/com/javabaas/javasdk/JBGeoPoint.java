package com.javabaas.javasdk;

/**
 * Created by zangyilin on 2018/12/5.
 */
public class JBGeoPoint {
    static double EARTH_MEAN_RADIUS_KM = 6378.14D;
    static double ONE_KM_TO_MILES = 1.609344D;
    private double latitude;
    private double longitude;
    /**
     * 维度
     */
    static final String LATITUDE_KEY = "latitude";
    /**
     * 经度
     */
    static final String LONGTITUDE_KEY = "longitude";

    public JBGeoPoint() {
        this.latitude = 0.0D;
        this.longitude = 0.0D;
    }

    /**
     *  计算本位置到另一位置距离（公里）
     * @param point 位置
     * @return 距离
     */
    public double distanceInKilometersTo(JBGeoPoint point) {
        float[] mResults = new float[2];
        computeDistanceAndBearing(this.latitude, this.longitude, point.latitude, point.longitude, mResults);

        return mResults[0] / 1000.0F;
    }

    /**
     *  计算本位置到另一位置距离（英里）
     * @param point 位置
     * @return 距离
     */
    public double distanceInMilesTo(JBGeoPoint point) {
        return distanceInKilometersTo(point) / ONE_KM_TO_MILES;
    }

    /**
     * 计算本位置到另一位置弧度
     *
     * @param point 位置
     * @return 弧度
     */
    public double distanceInRadiansTo(JBGeoPoint point) {
        return distanceInKilometersTo(point) / EARTH_MEAN_RADIUS_KM;
    }

    public JBGeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double l) {
        this.latitude = l;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double l) {
        this.longitude = l;
    }

    private static void computeDistanceAndBearing(double lat1, double lon1, double lat2, double lon2, float[] results) {
        int MAXITERS = 20;

        lat1 *= 0.017453292519943295D;
        lat2 *= 0.017453292519943295D;
        lon1 *= 0.017453292519943295D;
        lon2 *= 0.017453292519943295D;

        double a = 6378137.0D;
        double b = 6356752.3142D;
        double f = (a - b) / a;
        double aSqMinusBSqOverBSq = (a * a - b * b) / (b * b);

        double L = lon2 - lon1;
        double A = 0.0D;
        double U1 = Math.atan((1.0D - f) * Math.tan(lat1));
        double U2 = Math.atan((1.0D - f) * Math.tan(lat2));

        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);
        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;

        double sigma = 0.0D;
        double deltaSigma = 0.0D;
        double cosSqAlpha = 0.0D;
        double cos2SM = 0.0D;
        double cosSigma = 0.0D;
        double sinSigma = 0.0D;
        double cosLambda = 0.0D;
        double sinLambda = 0.0D;

        double lambda = L;
        for (int iter = 0; iter < MAXITERS; iter++) {
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
            double sinSqSigma = t1 * t1 + t2 * t2;
            sinSigma = Math.sqrt(sinSqSigma);
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = sinSigma == 0.0D ? 0.0D : cosU1cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1.0D - sinAlpha * sinAlpha;
            cos2SM = cosSqAlpha == 0.0D ? 0.0D : cosSigma - 2.0D * sinU1sinU2 / cosSqAlpha;

            double uSquared = cosSqAlpha * aSqMinusBSqOverBSq;
            A = 1.0D + uSquared / 16384.0D * (4096.0D + uSquared * (-768.0D + uSquared * (320.0D - 175.0D * uSquared)));

            double B = uSquared / 1024.0D * (256.0D + uSquared * (-128.0D + uSquared * (74.0D - 47.0D * uSquared)));

            double C = f / 16.0D * cosSqAlpha * (4.0D + f * (4.0D - 3.0D * cosSqAlpha));
            double cos2SMSq = cos2SM * cos2SM;
            deltaSigma = B * sinSigma * (cos2SM + B / 4.0D * (cosSigma * (-1.0D + 2.0D * cos2SMSq) - B / 6.0D * cos2SM * (-3.0D + 4.0D * sinSigma * sinSigma) * (-3.0D + 4.0D * cos2SMSq)));

            lambda = L + (1.0D - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SM + C * cosSigma * (-1.0D + 2.0D * cos2SM * cos2SM)));

            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0E-12D) {
                break;
            }
        }
        float distance = (float)(b * A * (sigma - deltaSigma));
        results[0] = distance;
        if (results.length > 1) {
            float initialBearing = (float)Math.atan2(cosU2 * sinLambda, cosU1 * sinU2 - sinU1 * cosU2 * cosLambda);
            initialBearing = (float)(initialBearing * 57.29577951308232D);
            results[1] = initialBearing;
            if (results.length > 2) {
                float finalBearing = (float)Math.atan2(cosU1 * sinLambda, -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda);
                finalBearing = (float)(finalBearing * 57.29577951308232D);
                results[2] = finalBearing;
            }
        }
    }
}
