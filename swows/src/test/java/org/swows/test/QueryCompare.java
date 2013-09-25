package org.swows.test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.jena.atlas.lib.Lib;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryVisitor;
import com.hp.hpl.jena.sparql.core.Prologue;
import com.hp.hpl.jena.sparql.util.NodeIsomorphismMap;

// Two queries comparison 

public class QueryCompare implements QueryVisitor
{
    private Query   query2 ;
    private boolean result = true ;
    static public boolean PrintMessages = false ;

    public static boolean equals(Query query1, Query query2)
    {
        if ( query1 == query2 ) return true ;
        
        query1.setResultVars() ;
        query2.setResultVars() ;
        QueryCompare visitor = new QueryCompare(query1) ;
        try {
            query2.visit(visitor) ;
        } catch ( ComparisonException ex)
        {
            return false ;
        }
        return visitor.isTheSame() ;
    }
    
    public QueryCompare(Query query2)
    {
        this.query2 = query2 ;
        
    }

    public void startVisit(Query query1)
    {  }  

    public void visitResultForm(Query query1)
    { check("Query result form", query1.getQueryType() == query2.getQueryType()) ; }

    public void visitPrologue(Prologue query1)
    {
        // This is after parsing so all IRIs in the query have been made absolute.
        // For two queries to be equal, their explicitly set base URIs must be the same. 
        
        String b1 = query1.explicitlySetBaseURI() ? query1.getBaseURI() : null ;
        String b2 = query2.explicitlySetBaseURI() ? query2.getBaseURI() : null ;        
        check("Base URIs", b1, b2) ;

        if ( query1.getPrefixMapping() == null &&
            query2.getPrefixMapping() == null )
            return ;
//        check("Prefixes", query1.getPrefixMapping().samePrefixMappingAs(query2.getPrefixMapping())) ;
    }

    public void visitSelectResultForm(Query query1)
    { 
        check("Not both SELECT queries", query2.isSelectType()) ;
        check("DISTINCT modifier",
              query1.isDistinct() == query2.isDistinct()) ;
        check("SELECT *", query1.isQueryResultStar() == query2.isQueryResultStar()) ;
        check("Result variables",   query1.getProject(), query2.getProject() ) ;
    }

    public void visitConstructResultForm(Query query1)
    {
        check("Not both CONSTRUCT queries", query2.isConstructType()) ;
        check("CONSTRUCT templates", 
              query1.getConstructTemplate().equalIso(query2.getConstructTemplate(), new NodeIsomorphismMap()) ) ;
    }

    public void visitDescribeResultForm(Query query1)
    {
        check("Not both DESCRIBE queries", query2.isDescribeType()) ;
        check("Result variables", 
              query1.getResultVars(), query2.getResultVars() ) ;
        check("Result URIs", 
              query1.getResultURIs(), query2.getResultURIs() ) ;
        
    }

    public void visitAskResultForm(Query query1)
    {
        check("Not both ASK queries", query2.isAskType()) ;
    }

    public void visitDatasetDecl(Query query1)
    {
        boolean b1 = Lib.equalsListAsSet(query1.getGraphURIs(), query2.getGraphURIs()) ;
        check("Default graph URIs", b1 ) ;
        boolean b2 = Lib.equalsListAsSet(query1.getNamedGraphURIs(), query2.getNamedGraphURIs()) ; 
        check("Named graph URIs", b2 ) ;
    }

    public void visitQueryPattern(Query query1)
    {
        if ( query1.getQueryPattern() == null &&
             query2.getQueryPattern() == null )
            return ;
        
        if ( query1.getQueryPattern() == null ) throw new ComparisonException("Missing pattern") ;
        if ( query2.getQueryPattern() == null ) throw new ComparisonException("Missing pattern") ;
        
        // The checking for patterns (elements) involves a potential
        // remapping of system-allocated variable names.
        // Assumes blank node variables only appear in patterns.
        check("Pattern", query1.getQueryPattern().equalTo(query2.getQueryPattern(), new NodeIsomorphismMap())) ;
    }

    public void visitGroupBy(Query query1)
    {
        check("GROUP BY", query1.getGroupBy(), query2.getGroupBy()) ;
    }
    
    public void visitHaving(Query query1) 
    {
        check("HAVING", query1.getHavingExprs(), query2.getHavingExprs()) ;
    }
    
    public void visitLimit(Query query1)
    {
        check("LIMIT", query1.getLimit() == query2.getLimit() ) ;
    }

     public void visitOrderBy(Query query1)
     {
         check("ORDER BY", query1.getOrderBy(), query2.getOrderBy() ) ;
     }

     public void visitOffset(Query query1)
    {
        check("OFFSET", query1.getOffset() == query2.getOffset() ) ;
    }

////    public void visitBindings(Query query1)
//    {
//        // Must be same order for now.
//        check("BINDINGS/variables", query1.getBindingVariables(), query2.getBindingVariables()) ;
//        check("BINDINGS/values", query1.getBindingValues(), query2.getBindingValues()) ;
//    }

    public void finishVisit(Query query1)
    {}
    
    private void check(String msg, Object obj1, Object obj2)
    {
        check(msg, Lib.equal(obj1,obj2)) ;
    }
    
    private void check(String msg, boolean b)
    {
        if ( !b )
        {
            if ( PrintMessages && msg != null )
                System.out.println("Different: "+msg) ;
            result = false ;
            throw new ComparisonException(msg) ;
        }
    }

    public boolean isTheSame() { return result ; }

	public void visitValues(Query arg0) {
		// TODO Auto-generated method stub
		
	}
}
