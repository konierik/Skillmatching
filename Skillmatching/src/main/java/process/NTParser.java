package process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

/**This class loads an nt-format (in memory) file and creates rdf/turtle triples from it.
 * It uses the JENA package for ontology handling*/
public class NTParser {
	
	private String file;
	private Model model= ModelFactory.createDefaultModel();
	private InputStream is;
	private OutputStream output;
	
	
	
	
	public NTParser(String file) {
		setFile(file);
	}
	
	public NTParser(InputStream streamy) {
		setIntputStream(streamy);
	}
	
	public static void main(String[] args) {
		OntModel testmodel= ModelFactory.createOntologyModel();
	    InputStream in = FileManager.get().open("https://github.com/konierik/O-N/raw/master/ontology/NTInstanceProperties.nt");
	    OutputStream out = null;
	    try {
			out= new FileOutputStream(new File("C:\\Users\\konierik\\Desktop\\Family_test\\NTparseJENA.ttl"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    
	    if (in != null) {
	        
	    	testmodel.setDynamicImports(true);
	        testmodel.addLoadedImport("https://github.com/konierik/O-N/raw/master/ontology/Family2.owl");
	        testmodel.setNsPrefix("family", "https://github.com/konierik/O-N/raw/master/ontology/Family2.owl#");
	        testmodel.setNsPrefix("", "https://github.com/konierik/O-N/raw/master/ontology/NTInstances.ttl#");
	        testmodel.setNsPrefix("owl","http://www.w3.org/2002/07/owl#");
	        testmodel.read(in, null, "N-TRIPLE");
	        Ontology ont = testmodel.createOntology("https://github.com/konierik/O-N/raw/master/ontology/NTInstanceProperties.ttl");
	        ont.addImport(testmodel.createResource("https://github.com/konierik/O-N/raw/master/ontology/Family2.owl"));
	       
	        testmodel.write(out, "TURTLE","https://github.com/konierik/O-N/raw/master/ontology/NTInstances.ttl");
	        
	    } else {
	        System.err.println("cannot read " + "");;
	    }
	}
	
	public void parseNT(String outputloaction) {
	    if (is != null) {
	        //model.read(is, null, "N-TRIPLE");
	        model.write(output, "TURTLE");
	        
	    } else {
	        System.err.println("cannot parse " + file+". Please set file/inputstream and outputstream.");;
	    }
	}
	
	public void readNTModel() {
	    if (is != null) {
	        model.read(is, null, "N-TRIPLE");
	    } else {
	    	System.err.println("cannot read " + file+". Please set file/inputstream and outputstream.");;
	    }
	}
	
	public void setIntputStream(InputStream streamy) {
		is=streamy;
	}
	
	public void setFile(String a) {
		file=a;
		is=FileManager.get().open(file);
		readNTModel();
	}
	
	public void setPrefix(String prefix, String uri) {
		model.setNsPrefix(prefix, uri);
	}
	
	public void setOutput(String outputsource) {
		try {
			output=new FileOutputStream(outputsource);
		} catch (FileNotFoundException e) {
			System.out.println("Could not create FileOutputStream for "+outputsource);
			e.printStackTrace();
		}
		
	}
	
}
