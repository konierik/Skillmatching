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
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import com.github.owlcs.ontapi.OntManagers;

import static org.junit.Assert.assertNotNull;
import static org.semanticweb.owlapi.search.Searcher.annotations;


public class OntoModeler {
	
	private String OSHPD ="https://github.com/OPEN-NEXT/WP3_Skillmatching/raw/main/ontology/OSHPD_schema.owl";
	private String skills="https://github.com/OPEN-NEXT/WP3_Skillmatching/raw/main/ontology/skills.owl";
	private String prefix="";	
	
	private OWLOntology onto;
	private OWLOntologyManager onto_man;
	private OWLDataFactory onto_df;
	private IRI iri;
	private IRI docIRI; //for local ontology creation
	private OWLAnnotationProperty classmapping;
	private OWLAnnotationProperty datapropertymapping;
	private OWLAnnotationProperty objectpropertymapping;
	private OWLAnnotationProperty identifier;
	private String IRIstring = "https://github.com/konierik/O-N/raw/master/ontology/Family2.owl";
	
	
	//Construtors
	/*
	public OntoModeler() throws OWLOntologyCreationException {

	}*/
	
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
	
	public void loadOnto(String FileLocationAndName) throws OWLOntologyCreationException {
	    // Loading the OWL file
		 onto_man = OntManagers.createManager();
	     onto_df = onto_man.getOWLDataFactory();
	     onto = onto_man.loadOntologyFromOntologyDocument(new File(FileLocationAndName));
	     iri =onto_man.getOntologyDocumentIRI(onto);
	     System.out.println("Loaded ontology iri: "+iri);
	     System.out.println("Get ontology iri: "+ onto_man.getOntologyDocumentIRI(onto).toString());
	}
	
	public void loadOnto() {
		try {
			onto_man = OWLManager.createOWLOntologyManager();
			onto = onto_man.loadOntology(iri);
			onto_df = onto.getOWLOntologyManager().getOWLDataFactory();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				Create ontology
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void createOnto(String FileLocationAndName) throws OWLOntologyCreationException {
	     onto_man = OWLManager.createOWLOntologyManager();
	     onto=onto_man.createOntology(iri);
	     assertNotNull(onto);
	 //set iri to the ontology
	     docIRI=IRI.create(FileLocationAndName);
	     onto_man.setOntologyDocumentIRI(onto, docIRI);
	 //map doc iri and onto iri
	     onto_man.getIRIMappers().add(new SimpleIRIMapper(iri,docIRI));
	 //get datafactory for onto handling (properties, instances, ...)
	     onto_df = onto_man.getOWLDataFactory();
	     
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				import ontology
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public void importFromURL(String ontoURL) {
		OWLImportsDeclaration importDeclaration = onto_df.getOWLImportsDeclaration(IRI.create(ontoURL));
		AddImport impi= new AddImport(onto, importDeclaration);
		onto_man.applyChange(impi);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				save ontology
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	
	public ArrayList<ArrayList<String>> getDatapropertiesAnnotations(){
		ArrayList<ArrayList<String>> datasannotations = new ArrayList<ArrayList<String>>();
		ArrayList <String> datas= new ArrayList<String>(); 
		ArrayList <String> annotations= new ArrayList <String>();
		ArrayList <String> domainpointer=new ArrayList<String>();
		ArrayList <String> identpointer= new ArrayList<String>();
		for(OWLDataProperty dp: onto.dataPropertiesInSignature().collect(Collectors.toSet())) {
			if(!getAnnotations(onto,dp.getIRI(),datapropertymapping).get(0).isEmpty()) {
				datas.add(dp.getIRI().toString());
				annotations.addAll(getAnnotations(onto, dp.getIRI(), datapropertymapping)); 
				domainpointer.addAll(getAnnotations(onto,IRI.create(getDatapropertyDomain(dp.getIRI())), classmapping));
				identpointer.addAll(getAnnotations(onto,IRI.create(getDatapropertyDomain(dp.getIRI())), identifier));
			}
        }
		datasannotations.add(0,datas);
		datasannotations.add(1,annotations);
		datasannotations.add(2,domainpointer);
		datasannotations.add(3,identpointer);
		return datasannotations;
	}
	
	
	//Objectproperties
	public ArrayList<ArrayList<String>> getObjectpropertiesAnnotations(){
		ArrayList<ArrayList<String>> objectsannotations = new ArrayList<ArrayList<String>>();
		ArrayList <String> objects= new ArrayList<String>(); 
		ArrayList <String> annotations= new ArrayList <String>();
		ArrayList <String> domainpointer=new ArrayList<String>();
		ArrayList <String> identpointer= new ArrayList<String>();
		for(OWLObjectProperty op: onto.objectPropertiesInSignature().collect(Collectors.toSet())) {
			if(!getAnnotations(onto,op.getIRI(),objectpropertymapping).get(0).isEmpty()) {
				objects.add(op.getIRI().toString());	
				//get annotation in the class
				annotations.addAll(getAnnotations(onto, op.getIRI(), objectpropertymapping));  
				domainpointer.addAll(getAnnotations(onto,IRI.create(getObjectPropertyDomain(op.getIRI())), classmapping));
				identpointer.addAll(getAnnotations(onto,IRI.create(getObjectPropertyDomain(op.getIRI())), identifier));
        	}
		}
		objectsannotations.add(0,objects);
		objectsannotations.add(1,annotations);
		objectsannotations.add(2,domainpointer);
		objectsannotations.add(3,identpointer);
		return objectsannotations;
	}
	
	
	//Classes
	public  ArrayList<ArrayList<String>> getClassesAnnotations(){
		ArrayList<ArrayList<String>> classesannotations = new ArrayList<ArrayList<String>>();
		ArrayList <String> classes= new ArrayList<String>(); 
		ArrayList <String> annotations= new ArrayList <String>();
		ArrayList <String> ident=new ArrayList<String>();
		for( OWLClass oc : onto.classesInSignature().collect(Collectors.toSet() ) ) {				
	        if(!getAnnotations(onto,oc.getIRI(),classmapping).get(0).isEmpty()) {
	        	classes.add(oc.getIRI().toString());
	        	annotations.add(getAnnotations(onto, oc.getIRI(), classmapping).get(0));
	        	ident.add(getAnnotations(onto, oc.getIRI(), identifier).get(0));
	        }	
		}
		classesannotations.add(0,classes);
		classesannotations.add(1,annotations);
		classesannotations.add(2, ident);
		return classesannotations;
	}
	
	//general annotation axioms for all getAnnotation methods
	//gets annotaions: input: source ontology, iri from property that has the annotation, anntoation property to search
	public ArrayList <String> getAnnotations(OWLOntology onto, IRI iri, OWLAnnotationProperty annprop) {
        ArrayList <String> listo= new ArrayList <String> ();	
		for (OWLAnnotation annotation : annotations(
         		 onto.annotationAssertionAxioms(iri), annprop).collect(Collectors.toSet())) {
              if (annotation.getValue() instanceof OWLLiteral) {
                  OWLLiteral val = (OWLLiteral) annotation.getValue();
                  listo.add(val.getLiteral().toString());
                  //System.out.println("\t Annotationproperty: "+val.getLiteral().toString());
              }
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
	//				get Domain of property
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getDatapropertyDomain(IRI iri) {
		OWLDataProperty property=onto_df.getOWLDataProperty(iri); 
		String string ="";
		for( OWLDataPropertyDomainAxiom dpa : onto.dataPropertyDomainAxioms(property).collect( Collectors.toSet() ) ) {				
			string=dpa.getDomain().asOWLClass().getIRI().toString();
		}
		return string;
	}
	
	public String getObjectPropertyDomain(IRI iri) {
		OWLObjectProperty property = onto_df.getOWLObjectProperty(iri);
		String string="";
		for( OWLObjectPropertyDomainAxiom dpa : onto.objectPropertyDomainAxioms(property).collect( Collectors.toSet() ) ) {				
			string=dpa.getDomain().asOWLClass().getIRI().toString();
		}
		return string;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				Instantiation methods
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	public void instantiateClass(String instance, String Class) {
		OWLClass domain = onto_df.getOWLClass(Class);
		OWLIndividual range = onto_df.getOWLNamedIndividual(instance);
		OWLAxiom assertion=onto_df.getOWLClassAssertionAxiom(domain,range);
		onto.add(assertion);
		//AddAxiom addAxiomChange =new AddAxiom (ontology, assertion);
		//m.applyChange(addAxiomChange);
		
	}
	
	public void instantiateDataProperty(String subject, String predicate, String object) {
		OWLIndividual domain = onto_df.getOWLNamedIndividual(subject);
		OWLLiteral range = onto_df.getOWLLiteral(object);
		OWLDataProperty property=onto_df.getOWLDataProperty(predicate);
		OWLAxiom assertion=onto_df.getOWLDataPropertyAssertionAxiom(property,domain,range);
		AddAxiom addAxiomChange =new AddAxiom (onto, assertion);
		onto_man.applyChange(addAxiomChange);
	}	
	
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
	public void setClassIdent(String mapping) {
		identifier=onto_df.getOWLAnnotationProperty(IRIstring+"#"+mapping);		
	}
	public void setIRI(String iristring) {
		iri =IRI.create(iristring);
		IRIstring=iristring;
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