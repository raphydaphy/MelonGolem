package tamaized.melongolem;

import java.io.*;
import java.util.Properties;

public class MelonConfig
{
	protected static final Properties defaultValues = new Properties();

	static
	{
		defaultValues.setProperty(Constants.CONFIG_HEALTH, "8");
		defaultValues.setProperty(Constants.CONFIG_SLICE_DAMAGE, "4");
		defaultValues.setProperty(Constants.CONFIG_HATS, "1");
		defaultValues.setProperty(Constants.CONFIG_EATS, "1");
		defaultValues.setProperty(Constants.CONFIG_HEAL, "1");
		defaultValues.setProperty(Constants.CONFIG_TEHNUT_MODE, "1");

	}

	public double health = 8;
	public float damage = 4;
	public boolean hats = true;
	public boolean eats = true;
	public float heal = 1;
	public boolean tehnutMode = false;

	protected String fileName;

	public MelonConfig(String fileName)
	{
		this.fileName = fileName;
	}

	public void read()
	{
		Properties properties = new Properties(defaultValues);

		try
		{
			FileReader configReader = new FileReader(fileName);
			properties.load(configReader);
			configReader.close();
		} catch (FileNotFoundException ignored)
		{
		} catch (IOException e)
		{
			System.out.println("Failed to save config file with name " + fileName);
			e.printStackTrace();
		}

		health = Double.parseDouble(properties.getProperty(Constants.CONFIG_HEALTH));
		damage = Float.parseFloat(properties.getProperty(Constants.CONFIG_SLICE_DAMAGE));
		hats = Integer.parseInt(properties.getProperty(Constants.CONFIG_HATS)) != 0;
		eats = Integer.parseInt(properties.getProperty(Constants.CONFIG_EATS)) != 0;
		heal = Float.parseFloat(properties.getProperty(Constants.CONFIG_HEAL));
		tehnutMode = Integer.parseInt(properties.getProperty(Constants.CONFIG_TEHNUT_MODE)) != 0;

		save();
	}

	public void save()
	{
		Properties properties = new Properties();
		properties.setProperty(Constants.CONFIG_HEALTH, Double.toString(health));
		properties.setProperty(Constants.CONFIG_SLICE_DAMAGE, Float.toString(damage));
		properties.setProperty(Constants.CONFIG_HATS, hats ? "1" : "0");
		properties.setProperty(Constants.CONFIG_EATS, eats ? "1" : "0");
		properties.setProperty(Constants.CONFIG_HEAL, Float.toString(heal));
		properties.setProperty(Constants.CONFIG_TEHNUT_MODE, tehnutMode ? "1" : "0");

		try
		{
			File config = new File(fileName);
			boolean existed = config.exists();
			File parentDir = config.getParentFile();
			if (!parentDir.exists()) parentDir.mkdirs();

			FileWriter configWriter = new FileWriter(config);
			properties.store(configWriter, null);
			configWriter.close();

			if (!existed) System.out.println("Created config file for Melon Golem");
		} catch (IOException e)
		{
			System.out.println("Failed to write to the config file " + fileName);
			e.printStackTrace();
		}
	}
}