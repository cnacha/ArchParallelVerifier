package nz.aucklanduni.archchecker.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.google.gson.Gson;

import nz.aucklanduni.archchecker.core.DiagramParser;
import nz.aucklanduni.archchecker.object.ClassDiagram;
import nz.aucklanduni.archchecker.object.Dependency;
import nz.aucklanduni.model.ComponentConfig;

public class DiagramToJSONConverter {
	private static String outputJSONFile = "output/system/models.txt";
	private static void convert(String modelName, PrintWriter writer) {
		
		DiagramParser parser = new DiagramParser();
		try {
			String modelFile = "input/classdiagram/"+modelName+".ucls";
			
			ClassDiagram cd = parser.process(modelFile);
			System.out.println(modelName+" "+cd.getPackageList().size());
			List<ComponentConfig> comps = new ArrayList<ComponentConfig>();
			
			// loop through all packages
			for(nz.aucklanduni.archchecker.object.Package pkg: cd.getPackageList()) {
			//	System.out.println(pkg.getId()+" "+pkg.getName());
				
				// create Component config and parse to JSON
				ComponentConfig comp = new ComponentConfig();
				comp.setId(pkg.getId());
				List<Dependency> deps = cd.getDependencyBySource(pkg.getId());
				int[] calls = new int[deps.size()];
				int i=0;
				for(Dependency dep : deps) {
					calls[i] = dep.getTarget().getRefId();
					i++;
				}
				comp.setCalls(calls);
				comps.add(comp);
				
			}
			Gson gson = new Gson();
			
			//System.out.println(gson.toJson(comps));
			writer.write(gson.toJson(comps)+"\n");
			
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String[] modSets = { "activiti", "hibernate", "hsqldb", "log4j", "springbeans", "springwebmvc", "xerces","xwork" };
		try {
			PrintWriter writer = new PrintWriter(new File(outputJSONFile));
			for(String mod : modSets) {
				convert(mod, writer);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
