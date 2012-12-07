/*******************************************************************************
 * Copyright 2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package emlab.repository;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import emlab.domain.market.ClearingPoint;
import emlab.domain.market.CommodityMarket;
import emlab.domain.technology.Substance;

/**
 * @author JCRichstein
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/emlab-test-context.xml"})
@Transactional
public class RepositoryTesting {
	
	
	Logger logger = Logger.getLogger(RepositoryTesting.class);

	
	//------- clearingPointRepository ---------
	@Autowired ClearingPointRepository clearingPointRepository;
	
	@Test
	public void testfindAllClearingPointsForSubstanceTradedOnCommodityMarkesAndTimeRange(){
		double[][] input = {{0,1},{1,1.1},{2,1.21},{3,1.331},{4,1.4641}};
		Substance substance = new Substance();
		substance.persist();
		CommodityMarket market = new CommodityMarket();
		market.setSubstance(substance);
		market.persist();
		Map<Integer, Double> inputMap = new HashMap();
		for (double[] d : input) {
			ClearingPoint cp = new ClearingPoint();
			cp.setTime((long) d[0]);
			cp.setPrice(d[1]);
			cp.setAbstractMarket(market);
			cp.persist();
			inputMap.put(new Integer((int) d[0]), d[1]);
		}

		//Testing selection of only first one, starting with negative value
		Iterable<ClearingPoint> cps = clearingPointRepository.findAllClearingPointsForSubstanceTradedOnCommodityMarkesAndTimeRange(substance, -2l, 0l);
		assertTrue(cps.iterator().next().getPrice() == 1);
		
		cps = clearingPointRepository.findAllClearingPointsForSubstanceTradedOnCommodityMarkesAndTimeRange(substance, -2l, 4l);
		for(ClearingPoint cp : cps){
			assertTrue(cp.getPrice() == inputMap.get(new Integer((int) cp.getTime())));
			//logger.warn(new Double(cp.getPrice()).toString() + "==" + inputMap.get(new Integer((int) cp.getTime())).toString());
		}
	}
	
	//SubstanceRepository
	@Autowired SubstanceRepository substanceRepository;
	
	@Test
	public void testfindAllSubstancesTradedOnCommodityMarkets(){
		Substance coal = new Substance();
		coal.setName("Coal");
		coal.persist();
		Substance co2 = new Substance();
		co2.persist();
		CommodityMarket market = new CommodityMarket();
		market.setSubstance(coal);
		market.persist();
		
		Iterable<Substance> substancesInDB = substanceRepository.findAllSubstancesTradedOnCommodityMarkets();
		int count = 0;
		for(Substance substance : substancesInDB){
			count++;
			assertTrue(substance.getName().equals("Coal"));
		}
		assertTrue(count == 1);
	}
	
	

}