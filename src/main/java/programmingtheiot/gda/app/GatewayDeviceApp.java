/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 */

package programmingtheiot.gda.app;

import org.apache.commons.cli.*;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main GDA application.
 */
public class GatewayDeviceApp
{
    // static
    private static final Logger _Logger =
        Logger.getLogger(GatewayDeviceApp.class.getName());

    // private var's
    private String configFile = ConfigConst.DEFAULT_CONFIG_FILE_NAME;
    private DeviceDataManager dataMgr = null;

    // constructors
    public GatewayDeviceApp()
    {
        super();
        _Logger.info("Initializing GDA...");
    }

    // static
    public static void main(String[] args)
    {
        Map<String, String> argMap = parseArgs(args);

        if (argMap.containsKey(ConfigConst.CONFIG_FILE_KEY)) {
            System.setProperty(ConfigConst.CONFIG_FILE_KEY, argMap.get(ConfigConst.CONFIG_FILE_KEY));
        }

        GatewayDeviceApp gwApp = new GatewayDeviceApp();
        gwApp.startApp();

        // --- Run indefinitely until manually stopped ---
        try {
            while (true) {
                Thread.sleep(2000L);
            }
        } catch (InterruptedException e) {
            // ignore
        }

        // If the loop is ever broken, stop cleanly
        gwApp.stopApp(0);
    }

    private static Map<String, String> parseArgs(String[] args)
    {
        Map<String, String> argMap = new HashMap<String, String>();

        if (args != null && args.length > 0)  {
            CommandLineParser parser = new DefaultParser();
            Options options = new Options();

            options.addOption("c", true, "The relative or absolute path of the config file.");

            try {
                CommandLine cmdLineArgs = parser.parse(options, args);

                if (cmdLineArgs.hasOption("c")) {
                    argMap.put(ConfigConst.CONFIG_FILE_KEY, cmdLineArgs.getOptionValue("c"));
                } else {
                    _Logger.info("No custom config file specified. Using default.");
                }
            } catch (ParseException e) {
                _Logger.warning("Failed to parse command line args. Ignoring - using defaults.");
            }
        }

        return argMap;
    }

    // public methods
    public void startApp()
    {
        _Logger.info("Starting GDA...");

        try {
            if (! ConfigUtil.getInstance().getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.TEST_EMPTY_APP_KEY,false)) {
                this.dataMgr = new DeviceDataManager();
            }

            if (this.dataMgr != null) {
                this.dataMgr.startManager();
            }

            _Logger.info("GDA started successfully.");
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to start GDA. Exiting.", e);
            stopApp(-1);
        }
    }

    public void stopApp(int code)
    {
        _Logger.info("Stopping GDA...");

        try {
            if (this.dataMgr != null) {
                this.dataMgr.stopManager();
            }

            _Logger.log(Level.INFO, "GDA stopped successfully with exit code {0}.", code);
        } catch (Exception e) {
            _Logger.log(Level.SEVERE, "Failed to cleanly stop GDA. Exiting.", e);
        }

        System.exit(code);
    }
}
