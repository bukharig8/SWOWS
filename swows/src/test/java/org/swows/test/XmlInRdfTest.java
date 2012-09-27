/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.swows.graph.events.DynamicGraph;
import org.swows.graph.events.DynamicGraphFromGraph;
import org.swows.graph.events.EventManager;
import org.swows.xmlinrdf.DomDecoder2;
import org.swows.xmlinrdf.DomEncoder;
import org.swows.xmlinrdf.DomEncoder2;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class XmlInRdfTest {

	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
//		String xmlUri = "/home/miguel/Scaricati/there.is.only.xul";
		String xmlUri = "/home/miguel/worldInfo2/svg/BlankMapWithRadioBox.svg";
		
//		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//		Document document = documentBuilder.parse(xmlUri);
		InputSource inputXML = new InputSource(xmlUri);
		
		System.out.println("*** Input XML ***");
		
		XMLSerializer serializer = new XMLSerializer(System.out,new OutputFormat());
		// As a DOM Serializer
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler( serializer );
			//xmlReader.setFeature("XML 2.0", true);
			xmlReader.parse(inputXML);
		} catch(SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		System.out.println("");
		System.out.println("*** Output RDF ***");
		
		Graph rdfGraph = DomEncoder2.encode(inputXML, xmlUri);
		Model model = ModelFactory.createModelForGraph(rdfGraph);
		OutputStream out = new FileOutputStream("/home/miguel/BlankMapWithRadioBox.n3");
		model.write(out, "N3");
		//model.write(System.out);
		
		Document doc = DomDecoder2.decodeOne(new DynamicGraphFromGraph(rdfGraph));
		PrintWriter out2 =
				new PrintWriter(
						new FileOutputStream("/home/miguel/BlankMapWithRadioBox.xml") );
		XMLSerializer serializer2 = new XMLSerializer(out2,new OutputFormat());
		// As a DOM Serializer
		try {
			serializer2.serialize(doc);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		out2.flush();
		out2.close();
	}

}
