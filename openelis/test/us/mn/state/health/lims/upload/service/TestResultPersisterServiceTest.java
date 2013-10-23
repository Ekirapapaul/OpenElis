package us.mn.state.health.lims.upload.service;

import org.bahmni.feed.openelis.utils.AuditingService;
import org.junit.Before;
import org.mockito.Mock;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.upload.sample.CSVSample;
import us.mn.state.health.lims.upload.sample.CSVTestResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestResultPersisterServiceTest {
    @Mock
    private SamplePersisterService samplePersisterService;
    @Mock
    private SampleHumanPersisterService sampleHumanPersisterService;
    @Mock
    private AnalysisPersisterService analysisPersisterService;
    @Mock
    private SampleItemPersisterService sampleItemPersisterService;
    @Mock
    private ResultPersisterService resultPersisterService;
    @Mock
    private PatientDAO patientDAO;
    @Mock
    private AuditingService auditingService;
    @Mock
    private TestDAO testDAO;

    private TestResultPersisterService testResultPersisterService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        testResultPersisterService = new TestResultPersisterService(samplePersisterService, sampleHumanPersisterService, analysisPersisterService, sampleItemPersisterService, resultPersisterService, patientDAO, testDAO, auditingService);
    }

    @org.junit.Test
    public void shouldPersistAllEntitiesForTestResults() throws Exception {
        String sampleDate = "25-02-2012";
        final String sampleSource = "source";
        String testName1 = "test1";
        String testName2 = "test2";
        String testName3 = "test3";
        String result1 = "someValueForValue1";
        String result2 = "someValueForTest2";
        String result3 = "";
        List<CSVTestResult> testResults = Arrays.asList(new CSVTestResult(testName1, result1), new CSVTestResult(testName2, result2), new CSVTestResult(testName3, result3));
        String accessionNumber = "123";
        Sample sample = new Sample();
        sample.setId("1");
        String patientRegistrationNumber = "patientRegistrationNumber";
        String patientId = "patientId";
        String healthCenter = "gan";
        SampleHuman sampleHuman = new SampleHuman();
        sampleHuman.setPatientId(patientId);
        Patient patient = new Patient();
        patient.setId(patientId);
        Test test1 = new Test();
        test1.setTestName(testName1);
        Test test2 = new Test();
        test2.setTestName(testName2);
        Test test3 = new Test();
        test3.setTestName(testName3);
        SampleItem sampleItem1 = new SampleItem();
        SampleItem sampleItem2 = new SampleItem();
        SampleItem sampleItem3 = new SampleItem();
        Analysis analysis1 = new Analysis();
        Analysis analysis2 = new Analysis();
        Analysis analysis3 = new Analysis();
        CSVSample csvSample = new CSVSample(healthCenter, patientRegistrationNumber, accessionNumber, sampleDate, sampleSource, testResults);
        patientRegistrationNumber = healthCenter + patientRegistrationNumber;

        String sysUserId = "123";
        when(auditingService.getSysUserId()).thenReturn(sysUserId);
        when(samplePersisterService.save(csvSample, sysUserId)).thenReturn(sample);
        when(sampleHumanPersisterService.save(sample.getId(), patientRegistrationNumber, sysUserId)).thenReturn(sampleHuman);
        when(patientDAO.getPatientById(patientId)).thenReturn(patient);
        when(testDAO.getTestByName(testName1)).thenReturn(test1);
        when(testDAO.getTestByName(testName2)).thenReturn(test2);
        when(testDAO.getTestByName(testName3)).thenReturn(test3);
        when(sampleItemPersisterService.save(sample, test1, sysUserId)).thenReturn(sampleItem1);
        when(sampleItemPersisterService.save(sample, test2, sysUserId)).thenReturn(sampleItem2);
        when(sampleItemPersisterService.save(sample, test3, sysUserId)).thenReturn(sampleItem3);
        when(analysisPersisterService.save(test1, sampleDate, sampleItem1, sysUserId)).thenReturn(analysis1);
        when(analysisPersisterService.save(test2, sampleDate, sampleItem2, sysUserId)).thenReturn(analysis2);
        when(analysisPersisterService.save(test3, sampleDate, sampleItem3, sysUserId)).thenReturn(analysis3);

        testResultPersisterService.persist(csvSample);

        verify(samplePersisterService).save(csvSample, sysUserId);
        verify(sampleHumanPersisterService).save(sample.getId(), patientRegistrationNumber, sysUserId);
        verify(sampleItemPersisterService).save(sample, test1, sysUserId);
        verify(sampleItemPersisterService).save(sample, test2, sysUserId);
        verify(sampleItemPersisterService).save(sample, test3, sysUserId);
        verify(analysisPersisterService).save(test1, sampleDate, sampleItem1, sysUserId);
        verify(analysisPersisterService).save(test2, sampleDate, sampleItem2, sysUserId);
        verify(analysisPersisterService).save(test3, sampleDate, sampleItem3, sysUserId);
        verify(resultPersisterService).save(analysis1, test1, result1, patient, sysUserId);
        verify(resultPersisterService).save(analysis2, test2, result2, patient, sysUserId);
        verifyNoMoreInteractions(resultPersisterService);
    }
}
