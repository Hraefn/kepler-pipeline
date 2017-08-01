/*
 * Copyright 2017 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All Rights Reserved.
 * 
 * This file is available under the terms of the NASA Open Source Agreement
 * (NOSA). You should have received a copy of this agreement with the
 * Kepler source code; see the file NASA-OPEN-SOURCE-AGREEMENT.doc.
 * 
 * No Warranty: THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY
 * WARRANTY OF ANY KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY,
 * INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE
 * WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR FREEDOM FROM
 * INFRINGEMENT, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL BE ERROR
 * FREE, OR ANY WARRANTY THAT DOCUMENTATION, IF PROVIDED, WILL CONFORM
 * TO THE SUBJECT SOFTWARE. THIS AGREEMENT DOES NOT, IN ANY MANNER,
 * CONSTITUTE AN ENDORSEMENT BY GOVERNMENT AGENCY OR ANY PRIOR RECIPIENT
 * OF ANY RESULTS, RESULTING DESIGNS, HARDWARE, SOFTWARE PRODUCTS OR ANY
 * OTHER APPLICATIONS RESULTING FROM USE OF THE SUBJECT SOFTWARE.
 * FURTHER, GOVERNMENT AGENCY DISCLAIMS ALL WARRANTIES AND LIABILITIES
 * REGARDING THIRD-PARTY SOFTWARE, IF PRESENT IN THE ORIGINAL SOFTWARE,
 * AND DISTRIBUTES IT "AS IS."
 * 
 * Waiver and Indemnity: RECIPIENT AGREES TO WAIVE ANY AND ALL CLAIMS
 * AGAINST THE UNITED STATES GOVERNMENT, ITS CONTRACTORS AND
 * SUBCONTRACTORS, AS WELL AS ANY PRIOR RECIPIENT. IF RECIPIENT'S USE OF
 * THE SUBJECT SOFTWARE RESULTS IN ANY LIABILITIES, DEMANDS, DAMAGES,
 * EXPENSES OR LOSSES ARISING FROM SUCH USE, INCLUDING ANY DAMAGES FROM
 * PRODUCTS BASED ON, OR RESULTING FROM, RECIPIENT'S USE OF THE SUBJECT
 * SOFTWARE, RECIPIENT SHALL INDEMNIFY AND HOLD HARMLESS THE UNITED
 * STATES GOVERNMENT, ITS CONTRACTORS AND SUBCONTRACTORS, AS WELL AS ANY
 * PRIOR RECIPIENT, TO THE EXTENT PERMITTED BY LAW. RECIPIENT'S SOLE
 * REMEDY FOR ANY SUCH MATTER SHALL BE THE IMMEDIATE, UNILATERAL
 * TERMINATION OF THIS AGREEMENT.
 */

package gov.nasa.kepler.mr.webui;

import gov.nasa.kepler.common.ModifiedJulianDate;
import gov.nasa.kepler.hibernate.dr.ConfigMap;
import gov.nasa.kepler.hibernate.dr.ConfigMapCrud;
import gov.nasa.kepler.mr.MrTimeUtil;

import java.util.Date;
import java.util.List;

/**
 * Provides methods for accessing the configuration map table.
 * 
 * @author Bill Wohler
 */
public class ConfigMapUtil extends AbstractUtil {

    public String retrieveConfigMaps() {
        dbPrepare();

        StringBuilder options = new StringBuilder();
        try {
            List<ConfigMap> configMaps = new ConfigMapCrud().retrieveAllConfigMaps();

            if (configMaps.size() == 0) {
                String errorText = "No configuration maps have been created.";
                return displayError(errorText);
            }

            // Generate select list option rows. The synchronization is needed
            // because Iso8601Formatter.dateTimeFormatter() is not thread-safe.
            synchronized (ConfigMapUtil.class) {
                boolean firstOption = true;
                for (ConfigMap configMap : configMaps) {
                    options.append("<option value=\"")
                        .append(configMap.getScConfigId())
                        .append('\"');
                    if (firstOption) {
                        options.append(" selected");
                        firstOption = false;
                    }
                    Date date = ModifiedJulianDate.mjdToDate(configMap.getMjd());
                    String dateString = MrTimeUtil.dateFormatter()
                        .format(date);
                    int id = configMap.getScConfigId();
                    // Trim trailing space only.
                    String display = toNbsp(
                        String.format("X%5d  %-22s", id, dateString)
                            .trim()).substring(1);
                    options.append(">")
                        .append(display)
                        .append("</option>\r");
                }
            }
        } catch (Exception e) {
            return displayError("Could not obtain config maps: ", e);
        }

        return options.toString();
    }
}