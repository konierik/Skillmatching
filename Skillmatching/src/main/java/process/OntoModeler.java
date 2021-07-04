package process;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import com.github.owlcs.ontapi.OntManagers;

import static org.junit.Assert.assertNotNull;
import static org.semanticweb.owlapi.search.Searcher.annotations;

/**A class using the OWL-API and JENA-API to handle ontologies.*/
public class OntoModeler {
	
	/**A string that contains the namespace & location of the OSHPD ontology representing a possible OSH project work landscape.*/
	private String OSHPD ="https://github.com/OPEN-NEXT/WP3_Skillmatching/raw/main/ontology/OSHPD_schema.owl";
	/**A string that contains the namespace & location of the skills ontology that is imported into the OSHPD ontology*/
	private String skills="https://github.com/OPEN-NEXT/WP3_Skillmatching/raw/main/ontology/skills.owl";
	/**A string containing several prefixes used in the ontologies.*/
	private String prefix="";	
	
	private OWLOntology onto;
	private OWLOntologyManager onto_man;
	private OWLDataFactory onto_df;
	//Variables for iri information
	private IRI iri;
	private String IRIstring = "https://github.com/konierik/O-N/raw/master/ontology/Family2.owl";
	private IRI docIRI; //for local ontology creation
	/**Annotation property that has json pointers to keys that are instantiated as the relating class.*/
	private OWLAnnotationProperty classmapping;
	/**Annotation property that has json pointers to keys that are instantiated as the relating dataproperty.*/
	private OWLAnnotationProperty datapropertymapping;
	/**Annotation property that has json pointers to keys that are instantiated as the relating objectproperty.*/
	private OWLAnnotationProperty objectpropertymapping;

	
	
	

	/**This method system.out.prints the content of any ArrayList(ArrayList(String)). 
	 * It has no other use than to simply display the structure of the individual lists that can be generated in this class.
	 * So in general this method is not used. */
	public void listOut(ArrayList<ArrayList<String>> list) {
		for (int i=0;i<list.get(0).size();i++) {
	    	for(int j=0;j<list.size();j++) {
	    		System.out.println("list["+j+"]["+i+"]: "+list.get(j).get(i));
	    	}
	    }
	}	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				Load ontology
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**This method loads an owl-ontology from a local file into the OntoModeler.
	 * @param FileLocationAndName String with ontology file path including filename and extension.*/
	public void loadOnto(String FileLocationAndName) throws OWLOntologyCreationException {
	    // Loading the OWL file
		 onto_man = OntManagers.createManager();
	     onto_df = onto_man.getOWLDataFactory();
	     onto = onto_man.loadOntologyFromOntologyDocument(new File(FileLocationAndName));
	     iri =onto_man.getOntologyDocumentIRI(onto);
	     IRIstring=iri.toString();
	     System.out.println("Loaded ontology iri: "+iri);
	     System.out.println("Get ontology iri: "+ onto_man.getOntologyDocumentIRI(onto).toString());
	}
	
	/**This method loads an owl-ontology from an accessible URL into the OntoModeler.*/
	public void loadOnto() {
		try {
			onto_man = OWLManager.createOWLOntologyManager();
			onto = onto_man.loadOntology(iri);
			onto_df = onto.getOWLOntologyManager().getOWLDataFactory();
		} catch (OWLOntologyCreationException e) {
			System.out.println("Did not load ontology from IRI. Try to load ontology as file.");
			e.printStackTrace();
			try {
				loadOnto(docIRI.toString());
				} catch(Exception ex) {
					e.printStackTrace();
				}
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				Create ontology
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**A method to create a new ontology locally.
	 * @param FileLocationAndName A string with the path where the new ontology should be saved including filename and extension.
	 * The FileLocationAndName will be set as document IRI of the ontology but will be also connected to the iri variable. 
	 * This variable can be set in another method setIRI(). In this way the ontology is accessible from its IRI but also from its document iri.*/
	public void createOnto(String FileLocationAndName) throws OWLOntologyCreationException {
	     onto_man = OWLManager.createOWLOntologyManager();
	     onto=onto_man.createOntology(iri);
	     assertNotNull(onto);
	 //set iri to the ontology
	     docIRI=IRI.create(FileLocationAndName);
	     onto_man.setOntologyDocumentIRI(onto, docIRI);
	 //map doc iri and ontology iri
	     onto_man.getIRIMappers().add(new SimpleIRIMapper(iri,docIRI));
	 //get datafactory from the ontology manager for further ontology handling (properties, instances, ...)
	     onto_df = onto_man.getOWLDataFactory();
	     
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				import ontology
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////	
	/**A method that adds an import statement into the ontology. The ontology to be imported should be accessible from the machine using the code.
	 * @param ontoURL URL string for the ontology to be imported.*/
	public void importFromURL(String ontoURL) {
	//creating an importdeclaration that states the import of the ontology from ontoURL
		OWLImportsDeclaration importDeclaration = onto_df.getOWLImportsDeclaration(IRI.create(ontoURL));
	//creating an importaxiom that integrates the importDeclaration into the wished ontology onto
		AddImport impi= new AddImport(onto, importDeclaration);
	//adding the import with the ontology manager
		onto_man.applyChange(impi);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				save ontology
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**Locally saves the ontology to the path and name of the given parameter.
	 * @param fileandpath String representing the path including filename and extension of the ontology to save.*/
	public void saveOntology(String fileandpath) throws IOException, OWLOntologyStorageException, OWLOntologyCreationException {
		System.out.println("Start saving ontology...");
		System.out.println("Ontology found for saving: "+onto);
		File output =new File(fileandpath);
		IRI Onto2save=IRI.create(output.toURI());
		onto_man.saveOntology(onto, Onto2save);

	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				getAnnotation methods
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//Dataproperties
	
	/**This method creates an Arraylist(Arraylist(String)) in a triples form 
	 * <br>[[dataproperty domain pointer][dataproperty iri][dataproperty pointer]] <br>
	 * for all dataproperties in the ontology that are annotated with a datapropertymapping. <P>
	 * The dataproperty domain pointer is a mapping annotation pointing to the class instance of the dataproperty.<br>
	 * The dataproperty iri is the iristring of the dataproperty. <br>
	 * The dataproperty pointer is a json pointer to the value of the dataproperty.
	 * */
	public ArrayList<ArrayList<String>> getDatapropertiesAnnotations(){
		ArrayList<ArrayList<String>> datasannotations = new ArrayList<ArrayList<String>>();
		ArrayList <String> datas= new ArrayList<String>(); 
		ArrayList <String> annotations= new ArrayList <String>();
		//ArrayList <String> domainpointer=new ArrayList<String>();
		ArrayList <String> identpointer= new ArrayList<String>();
		for(OWLDataProperty dp: onto.dataPropertiesInSignature().collect(Collectors.toSet())) {
			ArrayList<String> ans=getAnnotations(onto,dp.getIRI(),datapropertymapping);
			if(!ans.isEmpty()&&ans!=null&&ans.get(0).length()!=0) {
				datas.add(dp.getIRI().toString());
				annotations.add(getAnnotations(onto, dp.getIRI(), datapropertymapping).get(0)); 
				//domainpointer.add(getAnnotations(onto,IRI.create(getDatapropertyDomain(dp.getIRI())), classmapping).get(0));
				identpointer.addAll(getAnnotations(onto,IRI.create(getDatapropertyDomain(dp.getIRI())), classmapping));
			}
        }
		datasannotations.add(0,identpointer);
		datasannotations.add(1,datas);
		datasannotations.add(2,annotations);
		//datasannotations.add(2,domainpointer);
		
		return datasannotations;
	}
	
	
	//Objectproperties
	/**This method creates an Arraylist(Arraylist(String)) in a triples form 
	 * <br>[[objectproperty domain pointer][objectproperty iri][objectproperty pointer]] <br>
	 * for all objectproperties in the ontology that are annotated with an objectpropertymapping. <P>
	 * The objectproperty domain pointer is a mapping annotation pointing to the class instance of the objectproperty.<br>
	 * The objectproperty iri is the iristring of the objectproperty. <br>
	 * The objectproperty pointer is a json pointer to the value of the objectproperty.
	 * */
	public ArrayList<ArrayList<String>> getObjectpropertiesAnnotations(){
		ArrayList<ArrayList<String>> objectsannotations = new ArrayList<ArrayList<String>>();
		ArrayList <String> objects= new ArrayList<String>(); 
		ArrayList <String> annotations= new ArrayList <String>();
		//ArrayList <String> domainpointer=new ArrayList<String>();
		ArrayList <String> identpointer= new ArrayList<String>();
		for(OWLObjectProperty op: onto.objectPropertiesInSignature().collect(Collectors.toSet())) {
			ArrayList<String> ans=getAnnotations(onto,op.getIRI(),objectpropertymapping);
			if(!ans.isEmpty()&&ans!=null&&ans.get(0).length()!=0) {
				System.out.println(ans.toString() +" type: "+ ans.get(0).getClass());
				objects.add(op.getIRI().toString());	
				//get annotation in the class
				annotations.add(getAnnotations(onto, op.getIRI(), objectpropertymapping).get(0));  
				//domainpointer.add(getAnnotations(onto,IRI.create(getObjectPropertyDomain(op.getIRI())), classmapping).get(0));
				identpointer.addAll(getAnnotations(onto,IRI.create(getObjectPropertyDomain(op.getIRI())), classmapping));
        	}
		}
		objectsannotations.add(0,identpointer);
		objectsannotations.add(1,objects);
		objectsannotations.add(2,annotations);
		//objectsannotations.add(2,domainpointer);
		
		return objectsannotations;
	}
	
	/**This method takes the annotations array from getObjectproptertiesAnnotations() method and gathers data for class instantiation of the range, 
	 * in case those instances are not in the other data.*/
	public ArrayList<ArrayList<String>> getClassesFromObjectpropertyRange(ArrayList<ArrayList<String>> objectsannotations){
		ArrayList<ArrayList<String>> objectpropertyrangeclasses=new ArrayList<ArrayList<String>>();
		ArrayList<String> domain =objectsannotations.get(2);
		ArrayList<String> property= new ArrayList<String>();
		ArrayList<String> range = new ArrayList<String>();
		for (int i=0; i<objectsannotations.get(0).size();i++){
			range.add(getObjectPropertyRange(IRI.create(objectsannotations.get(1).get(i))));
			property.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		}
		
		objectpropertyrangeclasses.add(0,domain);
		objectpropertyrangeclasses.add(1,property);
		objectpropertyrangeclasses.add(2,range);
		
		return objectpropertyrangeclasses;
	}
	
	/**This method returns an Arraylist(Arraylist(String)) in triple form [[class pointer][rdfs:type][class iri]].*/
	public  ArrayList<ArrayList<String>> getClassesAnnotations(){
		ArrayList<ArrayList<String>> classesannotations = new ArrayList<ArrayList<String>>();
		ArrayList <String> classes= new ArrayList<String>(); 
		ArrayList <String> annotations= new ArrayList <String>();
		//ArrayList <String> ident=new ArrayList<String>();
		ArrayList <String> rdfsType=new ArrayList<String>();
		for( OWLClass oc : onto.classesInSignature().collect(Collectors.toSet() ) ) {				
	        ArrayList<String> ans=getAnnotations(onto,oc.getIRI(),classmapping);
			if(!ans.isEmpty()&&ans!=null&&ans.get(0).length()!=0) {
	        	classes.add(oc.getIRI().toString());
	        	annotations.add(getAnnotations(onto,oc.getIRI(), classmapping).get(0));
	        	rdfsType.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	        	//ident.add(getAnnotations(onto,oc.getIRI(), identifier).get(0));
	        }	
		}
		classesannotations.add(0,annotations);
		classesannotations.add(1,rdfsType);
		classesannotations.add(2,classes);
		
		//classesannotations.add(2, ident);
		return classesannotations;
	}
	
		
	//general annotation axioms for all getAnnotation methods
	//gets annotaions: input: source ontology, iri from property that has the annotation, anntoation property to search
	
	/**This method looks for values in annotation properties.
	 * @param onto	The input ontology to look for the annotations
	 * @param iri	The iri of the concept (class or property) that is annotated 
	 * @param annprop	The annotation property of interest*/
	public ArrayList <String> getAnnotations(OWLOntology onto, IRI iri, OWLAnnotationProperty annprop) {
        //output list
		ArrayList <String> listo= new ArrayList <String> ();	
		//run through all annotation axioms of the concept with IRI "iri" in the ontology, that is the same as annprop
		for (OWLAnnotation annotation : annotations(onto.annotationAssertionAxioms(iri), annprop).collect(Collectors.toSet())) {
             // if (annotation.getValue() instanceof OWLLiteral) {
                  OWLLiteral val = (OWLLiteral) annotation.getValue();
                  listo.add(val.getLiteral().toString());
                  //System.out.println("\t Annotationproperty: "+val.getLiteral().toString());
              //}
          }
		return listo;
	}
	


	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				get identifier for class of an annotationproperty
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	//this method gets the identifier of classes, e.g. when they are used as domain or range in data-/objectproperties
	/*
	public String getIdentPointer(OWLClass classy) {
		String out="";
		try {
			out=getAnnotations(onto, classy.getIRI(), identifier).toString();
		} catch(Exception e) {
			
		}
		return out;
	}
	
	public String getClassPointer(OWLClass classy) {
		String out="";
		try {
			out=getAnnotations(onto, classy.getIRI(), classmapping).toString();
		} catch(Exception e) {
			
		}
		return out;
	}
	
	public String getObjectpropertyPointer(OWLClass classy) {
		return getAnnotations(onto, classy.getIRI(), objectpropertymapping).get(0).toString();
	}
	public String getDatapropertyPointer(OWLClass classy) {
		return getAnnotations(onto, classy.getIRI(), datapropertymapping).get(0).toString();
	}
	*/
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				get Domain or Range of property
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**A method that returns the domain of a dataproperty as string.
	 * @param iri IRI of the property for which the domain is being searched.*/
	public String getDatapropertyDomain(IRI iri) {
		OWLDataProperty property=onto_df.getOWLDataProperty(iri); 
		String string ="";
		for( OWLDataPropertyDomainAxiom dpa : onto.dataPropertyDomainAxioms(property).collect( Collectors.toSet() ) ) {				
			try {
				string=dpa.getDomain().asOWLClass().getIRI().getIRIString();
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("\t"+dpa.getDomain().toString());
			}
		}
		return string;
	}
	
	/**A method that returns the domain of an objectproperty as string.
	 * @param iri FULL IRI of the property for which the domain is being searched.*/
	public String getObjectPropertyDomain(IRI iri) {
		OWLObjectProperty property = onto_df.getOWLObjectProperty(iri);
		String string="";
		for( OWLObjectPropertyDomainAxiom opa : onto.objectPropertyDomainAxioms(property).collect( Collectors.toSet() ) ) {				
			try {
				string=opa.getDomain().asOWLClass().getIRI().getIRIString();
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("\t"+opa.getDomain().toString());
			}
		}
		return string;
	}
	
	/**A method that returns the range of an objectproperty as string.
	 * @param iri FULL IRI of the property for which the range is being searched.*/
	public String getObjectPropertyRange(IRI iri) {
		OWLObjectProperty property = onto_df.getOWLObjectProperty(iri);
		String string="";
		for( OWLObjectPropertyRangeAxiom opa : onto.objectPropertyRangeAxioms(property).collect( Collectors.toSet() ) ) {				
			try {
				string=opa.getRange().asOWLClass().getIRI().getIRIString();
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("\t"+opa.getRange().toString());
			}
		}
		return string;
	}
	
	
	/**A method that returns the range of a dataproperty as string.
	 * @param iri IRI of the property for which the range is being searched.*/
	public String getDatapropertyRange(IRI iri) {
		OWLDataProperty property=onto_df.getOWLDataProperty(iri); 
		String string ="";
		for( OWLDataPropertyRangeAxiom dpa : onto.dataPropertyRangeAxioms(property).collect( Collectors.toSet() ) ) {				
			try {
				string=dpa.getRange().toString();
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("\t"+dpa.getRange().toString());
			}
		}
		return string;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				Instantiation methods
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**Instantiates the parameter instance as indvidual of the parameter Class.
	 * @param instance FULL IRI of the instance
	 * @param Class Full IRI of the class*/
	public void instantiateClass(String instance, String Class) {
		OWLClass domain = onto_df.getOWLClass(Class);
		OWLIndividual range = onto_df.getOWLNamedIndividual(instance);
		OWLAxiom assertion=onto_df.getOWLClassAssertionAxiom(domain,range);
		onto.add(assertion);
		//AddAxiom addAxiomChange =new AddAxiom (ontology, assertion);
		//m.applyChange(addAxiomChange);
		
	}
	
	/**Instantiates a dataproperty between the parameters (in format: domain property range).
	 * @param subject FULL IRI of the domain instance
	 * @param predicate Full IRI of the dataproperty
	 * @param object String of the dataproperty range value*/
	public void instantiateDataProperty(String subject, String predicate, String object) {
		OWLIndividual domain = onto_df.getOWLNamedIndividual(subject);
		OWLLiteral range = onto_df.getOWLLiteral(object);
		OWLDataProperty property=onto_df.getOWLDataProperty(predicate);
		OWLAxiom assertion=onto_df.getOWLDataPropertyAssertionAxiom(property,domain,range);
		AddAxiom addAxiomChange =new AddAxiom (onto, assertion);
		onto_man.applyChange(addAxiomChange);
	}	
	
	
	/**Instantiates an objectproperty between the parameters (in format: domain property range).
	 * @param subject FULL IRI of the domain instance
	 * @param predicate Full IRI of the objectproperty
	 * @param object FULL IRI of the range instance*/
	public void instantiateObjectProperty(String subject,String predicate, String object) {
		OWLIndividual domain = onto_df.getOWLNamedIndividual(subject); //gets a domain individual from the datafactory that has the form of the string "subject"
		OWLIndividual range = onto_df.getOWLNamedIndividual(object); //gets a range individual from the datafactory that has the form of the string "object"
		OWLObjectProperty property=onto_df.getOWLObjectProperty(predicate); //gets a objectproperty from the datafactory, that has the form of the string "predicate"
		OWLAxiom assertion=onto_df.getOWLObjectPropertyAssertionAxiom(property,domain,range); //creates an objectproperty-axiom with the defined two individuals and property 
		AddAxiom addAxiomChange =new AddAxiom (onto, assertion); 
		onto_man.applyChange(addAxiomChange);	
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				Setter
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public void setClassmapping(String mapping) {
		classmapping=onto_df.getOWLAnnotationProperty(IRIstring+"#"+mapping);		
	}
	
	public void setDatapropertymapping(String mapping) {
		datapropertymapping=onto_df.getOWLAnnotationProperty(IRIstring+"#"+mapping);		
	}
	
	public void setObjectpropertymapping(String mapping) {
		objectpropertymapping=onto_df.getOWLAnnotationProperty(IRIstring+"#"+mapping);		
	}
	
	public void setIRI(String iristring) {
		iri =IRI.create(iristring);
		IRIstring=iristring;
	}
	
	public void setDocIRI(String iri) {
		docIRI=IRI.create(iri);
	}
	
	public void setPrefix(String pf) {
		prefix=pf;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				Getter
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public IRI getIRI() {
		return iri;
	}
	
	public IRI getDocIRI() {
		return docIRI;
	}
	
	public String getIRIString() {
		return iri.toString();
	}
	
	public String getPrefixes() {
		return prefix;
	}
	
	
}