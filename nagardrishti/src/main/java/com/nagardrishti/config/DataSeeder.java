package com.nagardrishti.config;

import com.nagardrishti.entity.Project;
import com.nagardrishti.entity.Scheme;
import com.nagardrishti.repository.ProjectRepository;
import com.nagardrishti.repository.SchemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j @Component @RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SchemeRepository  schemeRepo;
    private final ProjectRepository projectRepo;

    @Override public void run(String... args) {
        seedSchemes();
        seedProjects();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SCHEMES  (Central + Karnataka State — all active as of 2025)
    // Pattern: if the ID doesn't exist yet, insert it. Safe to re-run anytime.
    // ─────────────────────────────────────────────────────────────────────────
    private void seedSchemes() {
        log.info("Seeding schemes (skipping existing)...");

        // ── EXISTING (SCH001-SCH010 kept for reference, safe no-ops) ────────

        if (!schemeRepo.existsById("SCH001"))
            schemeRepo.save(Scheme.builder().id("SCH001").name("Pradhan Mantri Matru Vandana Yojana").shortName("PMMVY")
                    .category("WOMEN").description("Maternity benefit program for pregnant and lactating mothers for first child.")
                    .ministryDepartment("Ministry of Women & Child Development").scope("CENTRAL")
                    .minAge(19).maxAge(50).gender("FEMALE").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(5000.0).benefitType("MULTIPLE_INSTALLMENTS")
                    .benefitDescription("Rs 5,000 in 3 installments for first living child.")
                    .howToApply("Register at nearest Anganwadi Centre or ASHA worker with Aadhaar and bank details.")
                    .documentsRequired(List.of("Aadhaar Card","Bank Passbook","MCP Card","Husband's Aadhaar"))
                    .officialWebsite("https://pmmvy.wcd.gov.in/").helplineNumber("181").launchDate("2017-01-01").build());

        if (!schemeRepo.existsById("SCH002"))
            schemeRepo.save(Scheme.builder().id("SCH002").name("Pradhan Mantri Ujjwala Yojana").shortName("PMUY")
                    .category("WOMEN").description("Free LPG connection to women from BPL families.")
                    .ministryDepartment("Ministry of Petroleum & Natural Gas").scope("CENTRAL")
                    .minAge(18).maxAge(100).gender("FEMALE").maxAnnualIncome(200000.0)
                    .eligibleCategories(List.of("BPL","SC","ST")).maritalStatus("ANY")
                    .benefitAmount(1600.0).benefitType("ONE_TIME").benefitDescription("Free LPG connection with first refill and stove.")
                    .howToApply("Apply at nearest LPG distributor with KYC documents.")
                    .documentsRequired(List.of("Aadhaar Card","BPL Certificate","Bank Account Details","Ration Card"))
                    .officialWebsite("https://pmuy.gov.in/").helplineNumber("1906").launchDate("2016-05-01").build());

        if (!schemeRepo.existsById("SCH003"))
            schemeRepo.save(Scheme.builder().id("SCH003").name("Indira Gandhi National Widow Pension Scheme").shortName("IGNWPS")
                    .category("WOMEN").description("Monthly pension for widows from BPL families.")
                    .ministryDepartment("Ministry of Rural Development").scope("CENTRAL")
                    .minAge(40).maxAge(79).gender("FEMALE").maxAnnualIncome(50000.0)
                    .eligibleCategories(List.of("BPL")).maritalStatus("WIDOW")
                    .benefitAmount(300.0).benefitType("MONTHLY").benefitDescription("Rs 300/month for BPL widows aged 40-79.")
                    .howToApply("Apply at District Social Welfare Office or Gram Panchayat.")
                    .documentsRequired(List.of("Aadhaar Card","Husband's Death Certificate","BPL Card","Bank Details"))
                    .officialWebsite("https://nsap.nic.in/").helplineNumber("1800-111-555").launchDate("2009-02-01").build());

        if (!schemeRepo.existsById("SCH004"))
            schemeRepo.save(Scheme.builder().id("SCH004").name("Gruha Lakshmi Scheme").shortName("GLS")
                    .category("WOMEN").description("Monthly Rs 2,000 financial assistance to woman head of every eligible family in Karnataka.")
                    .ministryDepartment("Karnataka Women & Child Development Dept").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(100).gender("FEMALE").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(2000.0).benefitType("MONTHLY")
                    .benefitDescription("Rs 2,000/month to woman head of family. 1.22 crore beneficiaries as of 2024-25.")
                    .howToApply("Register at Seva Sindhu portal or at registration camps.")
                    .documentsRequired(List.of("Aadhaar Card","Ration Card","Bank Passbook","Mobile Number"))
                    .officialWebsite("https://sevasindhu.karnataka.gov.in/").helplineNumber("1902").launchDate("2023-08-30").build());

        if (!schemeRepo.existsById("SCH005"))
            schemeRepo.save(Scheme.builder().id("SCH005").name("Beti Bachao Beti Padhao").shortName("BBBP")
                    .category("WOMEN").description("Scheme for welfare and education of girl child — linked with Sukanya Samriddhi Yojana.")
                    .ministryDepartment("Ministry of Women & Child Development").scope("CENTRAL")
                    .minAge(0).maxAge(10).gender("FEMALE").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("UNMARRIED").benefitAmount(0.0).benefitType("ONE_TIME")
                    .benefitDescription("Open Sukanya Samriddhi Account with 8.2% interest.")
                    .howToApply("Open account at any post office or authorised bank.")
                    .documentsRequired(List.of("Girl Child Birth Certificate","Parent's Aadhaar","Address Proof"))
                    .officialWebsite("https://wcd.nic.in/bbbp-schemes").helplineNumber("181").launchDate("2015-01-22").build());

        if (!schemeRepo.existsById("SCH006"))
            schemeRepo.save(Scheme.builder().id("SCH006").name("PM Kisan Samman Nidhi").shortName("PM-KISAN")
                    .category("FARMER").description("Direct income support of Rs 6,000/year to all landholding farmer families.")
                    .ministryDepartment("Ministry of Agriculture & Farmers Welfare").scope("CENTRAL")
                    .minAge(18).maxAge(100).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(6000.0).benefitType("YEARLY")
                    .benefitDescription("Rs 6,000/year in 3 installments of Rs 2,000 each.")
                    .howToApply("Register at pmkisan.gov.in or nearest Common Service Centre.")
                    .documentsRequired(List.of("Aadhaar Card","Land Records","Bank Account Details"))
                    .officialWebsite("https://pmkisan.gov.in/").helplineNumber("155261").launchDate("2019-02-24").build());

        if (!schemeRepo.existsById("SCH007"))
            schemeRepo.save(Scheme.builder().id("SCH007").name("Indira Gandhi National Old Age Pension Scheme").shortName("IGNOAPS")
                    .category("ELDERLY").description("Monthly pension for elderly BPL citizens.")
                    .ministryDepartment("Ministry of Rural Development").scope("CENTRAL")
                    .minAge(60).maxAge(120).gender("ALL").maxAnnualIncome(50000.0)
                    .eligibleCategories(List.of("BPL")).maritalStatus("ANY")
                    .benefitAmount(200.0).benefitType("MONTHLY").benefitDescription("Rs 200/month (60-79 yrs); Rs 500/month (80+ yrs).")
                    .howToApply("Apply at District Social Welfare Office or Gram Panchayat.")
                    .documentsRequired(List.of("Aadhaar Card","Age Proof","BPL Card","Bank Details"))
                    .officialWebsite("https://nsap.nic.in/").helplineNumber("1800-111-555").launchDate("2007-11-19").build());

        if (!schemeRepo.existsById("SCH008"))
            schemeRepo.save(Scheme.builder().id("SCH008").name("Pradhan Mantri Awas Yojana - Gramin").shortName("PMAY-G")
                    .category("HOUSING").description("Pucca houses for rural BPL homeless or kutcha-house families.")
                    .ministryDepartment("Ministry of Rural Development").scope("CENTRAL")
                    .minAge(18).maxAge(100).gender("ALL").maxAnnualIncome(300000.0)
                    .eligibleCategories(List.of("BPL","SC","ST")).maritalStatus("ANY")
                    .benefitAmount(150000.0).benefitType("MULTIPLE_INSTALLMENTS")
                    .benefitDescription("Rs 1.20 lakh (plains) or Rs 1.30 lakh (hilly areas).")
                    .howToApply("Registration through Gram Panchayat based on SECC-2011 data.")
                    .documentsRequired(List.of("Aadhaar Card","Bank Details","Job Card (MGNREGA)","SECC inclusion proof"))
                    .officialWebsite("https://pmayg.nic.in/").helplineNumber("1800-11-6446").launchDate("2016-11-20").build());

        if (!schemeRepo.existsById("SCH009"))
            schemeRepo.save(Scheme.builder().id("SCH009").name("National Scholarship Portal - Post Matric").shortName("NSP-PMS")
                    .category("STUDENT").description("Scholarship for SC/ST/OBC/Minority students in post-matric classes.")
                    .ministryDepartment("Ministry of Social Justice & Empowerment").scope("CENTRAL")
                    .minAge(14).maxAge(30).gender("ALL").maxAnnualIncome(250000.0)
                    .eligibleCategories(List.of("SC","ST","OBC","MINORITY")).maritalStatus("ANY")
                    .benefitAmount(25000.0).benefitType("YEARLY")
                    .benefitDescription("Tuition fees + maintenance allowance Rs 2,500-13,500/month.")
                    .howToApply("Apply online at scholarships.gov.in during academic year.")
                    .documentsRequired(List.of("Aadhaar Card","Caste Certificate","Income Certificate","Mark Sheets","Bank Details"))
                    .officialWebsite("https://scholarships.gov.in/").helplineNumber("0120-6619540").launchDate("2015-07-01").build());

        if (!schemeRepo.existsById("SCH010"))
            schemeRepo.save(Scheme.builder().id("SCH010").name("Ayushman Bharat - PM Jan Arogya Yojana").shortName("PMJAY")
                    .category("HEALTH").description("Health insurance cover of Rs 5 lakh per family per year.")
                    .ministryDepartment("Ministry of Health & Family Welfare").scope("CENTRAL")
                    .minAge(0).maxAge(120).gender("ALL").maxAnnualIncome(150000.0)
                    .eligibleCategories(List.of("BPL","SC","ST")).maritalStatus("ANY")
                    .benefitAmount(500000.0).benefitType("YEARLY")
                    .benefitDescription("Rs 5 lakh/year cashless hospitalization at empanelled hospitals.")
                    .howToApply("Check eligibility at pmjay.gov.in, get Ayushman Card at CSC.")
                    .documentsRequired(List.of("Aadhaar Card","Ration Card","SECC Card"))
                    .officialWebsite("https://pmjay.gov.in/").helplineNumber("14555").launchDate("2018-09-23").build());

        // ── NEW SCHEMES (SCH011 onwards) ─────────────────────────────────────

        if (!schemeRepo.existsById("SCH011"))
            schemeRepo.save(Scheme.builder().id("SCH011").name("Karnataka Shakti Scheme").shortName("SHAKTI")
                    .category("WOMEN").description("Free travel for all women on non-AC KSRTC/BMTC/NEKRTC/NWKRTC buses across Karnataka.")
                    .ministryDepartment("Karnataka Transport Department").scope("STATE").applicableState("Karnataka")
                    .minAge(0).maxAge(120).gender("FEMALE").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(0.0).benefitType("DAILY")
                    .benefitDescription("Free unlimited bus travel on government buses. Women show any valid ID to board.")
                    .howToApply("No separate application. Show government-issued ID (Aadhaar/Voter ID) at bus. Shakti Smart Card optional.")
                    .documentsRequired(List.of("Any Government ID (Aadhaar/Voter ID/PAN)"))
                    .officialWebsite("https://sevasindhu.karnataka.gov.in/").helplineNumber("1800-425-1663").launchDate("2023-06-11").build());

        if (!schemeRepo.existsById("SCH012"))
            schemeRepo.save(Scheme.builder().id("SCH012").name("Karnataka Gruha Jyoti Scheme").shortName("GRUHA-JYOTI")
                    .category("ELECTRICITY").description("Free electricity up to 200 units per month for all domestic households in Karnataka.")
                    .ministryDepartment("Karnataka Energy Department / ESCOMs").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(120).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(1000.0).benefitType("MONTHLY")
                    .benefitDescription("Up to 200 units free electricity every month. Saves approx Rs 1,000/month. 1.62 crore consumers registered in 2024-25.")
                    .howToApply("Apply on Seva Sindhu portal or ESCOM (BESCOM/MESCOM/GESCOM/HESCOM/CESC) website using RR Number and Aadhaar.")
                    .documentsRequired(List.of("Aadhaar Card","Electricity RR Number","Mobile Number","Domicile Certificate"))
                    .officialWebsite("https://sevasindhu.karnataka.gov.in/").helplineNumber("1912").launchDate("2023-07-01").build());

        if (!schemeRepo.existsById("SCH013"))
            schemeRepo.save(Scheme.builder().id("SCH013").name("Karnataka Anna Bhagya Scheme").shortName("ANNA-BHAGYA")
                    .category("FOOD").description("Free rice distribution to BPL families — 10 kg per person per month.")
                    .ministryDepartment("Karnataka Food & Civil Supplies Dept").scope("STATE").applicableState("Karnataka")
                    .minAge(0).maxAge(120).gender("ALL").eligibleCategories(List.of("BPL","SC","ST"))
                    .maritalStatus("ANY").benefitAmount(0.0).benefitType("MONTHLY")
                    .benefitDescription("10 kg rice per person per month free at ration shops. Provides food security to BPL families.")
                    .howToApply("Automatic for BPL ration card holders. Visit nearest Fair Price Shop.")
                    .documentsRequired(List.of("BPL Ration Card","Aadhaar Card"))
                    .officialWebsite("https://ahara.kar.nic.in/").helplineNumber("1967").launchDate("2023-07-15").build());

        if (!schemeRepo.existsById("SCH014"))
            schemeRepo.save(Scheme.builder().id("SCH014").name("Karnataka Yuva Nidhi Scheme").shortName("YUVA-NIDHI")
                    .category("YOUTH").description("Monthly unemployment allowance to educated unemployed youth in Karnataka for up to 2 years.")
                    .ministryDepartment("Skill Development, Entrepreneurship & Livelihood Dept, Karnataka").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(35).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(3000.0).benefitType("MONTHLY")
                    .benefitDescription("Rs 3,000/month for degree holders; Rs 1,500/month for diploma holders, for max 24 months.")
                    .howToApply("Apply online at Seva Sindhu portal. Verify degree on NAD portal first.")
                    .documentsRequired(List.of("Aadhaar Card","Degree/Diploma Certificate (NAD verified)","USN or Diploma Register Number","Bank Account Details","Domicile Certificate"))
                    .officialWebsite("https://sevasindhu.karnataka.gov.in/").helplineNumber("1800-5999-918").launchDate("2024-01-12").build());

        if (!schemeRepo.existsById("SCH015"))
            schemeRepo.save(Scheme.builder().id("SCH015").name("Rajiv Gandhi Housing Scheme (RGRHCL)").shortName("RGRHCL")
                    .category("HOUSING").description("Free pucca housing for homeless BPL families in Karnataka under RGRHCL.")
                    .ministryDepartment("Karnataka Housing Department / RGRHCL").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(100).gender("ALL").maxAnnualIncome(120000.0)
                    .eligibleCategories(List.of("BPL","SC","ST","OBC")).maritalStatus("ANY")
                    .benefitAmount(175000.0).benefitType("MULTIPLE_INSTALLMENTS")
                    .benefitDescription("Rs 1.75 lakh for house construction in rural areas, released in installments.")
                    .howToApply("Apply online at rgrhcl.kar.nic.in or at nearest Gram Panchayat.")
                    .documentsRequired(List.of("Aadhaar Card","BPL Certificate","Land Documents","Bank Details","Ration Card"))
                    .officialWebsite("https://rgrhcl.kar.nic.in/").helplineNumber("080-22032058").launchDate("2006-01-01").build());

        if (!schemeRepo.existsById("SCH016"))
            schemeRepo.save(Scheme.builder().id("SCH016").name("Karnataka Raitha Shakti Yojana").shortName("RAITHA-SHAKTI")
                    .category("FARMER").description("Diesel subsidy for farmers to run farm equipment and irrigation pumps in Karnataka.")
                    .ministryDepartment("Karnataka Agriculture Department").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(100).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(0.0).benefitType("YEARLY")
                    .benefitDescription("Diesel subsidy directly credited to farmer bank accounts. Rs 400 crore total budget allocated.")
                    .howToApply("Register via Seva Sindhu portal or nearest Raitha Samparka Kendra with land and Aadhaar details.")
                    .documentsRequired(List.of("Aadhaar Card","Land Records (RTC)","Bank Account Details","PM-KISAN Registration"))
                    .officialWebsite("https://raitamitra.karnataka.gov.in/").helplineNumber("155333").launchDate("2024-06-01").build());

        if (!schemeRepo.existsById("SCH017"))
            schemeRepo.save(Scheme.builder().id("SCH017").name("Karnataka Bhagyalakshmi Scheme (Thayi Bhagya)").shortName("BHAGYALAKSHMI")
                    .category("WOMEN").description("Financial support for girl child at birth and on attaining 18 years, to prevent female foeticide.")
                    .ministryDepartment("Karnataka Women & Child Development Dept").scope("STATE").applicableState("Karnataka")
                    .minAge(0).maxAge(0).gender("FEMALE").maxAnnualIncome(200000.0)
                    .eligibleCategories(List.of("BPL","SC","ST","OBC")).maritalStatus("ANY")
                    .benefitAmount(100097.0).benefitType("MULTIPLE_INSTALLMENTS")
                    .benefitDescription("Rs 19,300 at birth of first girl; Rs 1,00,097 when she turns 18.")
                    .howToApply("Register at nearest Anganwadi Centre within 1 year of birth of girl child.")
                    .documentsRequired(List.of("Girl Child Birth Certificate","Mother's Aadhaar","BPL/Income Certificate","Bank Details"))
                    .officialWebsite("https://wcdkarnataka.gov.in/").helplineNumber("080-22355984").launchDate("2006-08-15").build());

        if (!schemeRepo.existsById("SCH018"))
            schemeRepo.save(Scheme.builder().id("SCH018").name("Karnataka Mukhyamantri Anila Bhagya Scheme").shortName("ANILA-BHAGYA")
                    .category("WOMEN").description("Free LPG gas connection, stove and first refill for BPL families in Karnataka.")
                    .ministryDepartment("Karnataka Energy / Food & Civil Supplies Dept").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(100).gender("FEMALE").maxAnnualIncome(120000.0)
                    .eligibleCategories(List.of("BPL","SC","ST")).maritalStatus("ANY")
                    .benefitAmount(2000.0).benefitType("ONE_TIME")
                    .benefitDescription("Free LPG connection + stove + first gas refill for BPL households.")
                    .howToApply("Apply at nearest LPG distributor or via Seva Sindhu portal.")
                    .documentsRequired(List.of("Aadhaar Card","BPL Ration Card","Bank Details","Passport Size Photo"))
                    .officialWebsite("https://sevasindhu.karnataka.gov.in/").helplineNumber("1906").launchDate("2023-09-01").build());

        if (!schemeRepo.existsById("SCH019"))
            schemeRepo.save(Scheme.builder().id("SCH019").name("Gruha Arogya Scheme Karnataka").shortName("GRUHA-AROGYA")
                    .category("HEALTH").description("Free doorstep health screening and medicines for citizens across Karnataka.")
                    .ministryDepartment("Karnataka Health & Family Welfare Department").scope("STATE").applicableState("Karnataka")
                    .minAge(0).maxAge(120).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(0.0).benefitType("MONTHLY")
                    .benefitDescription("Free health check-up camp at doorstep, free medicines, referral to government hospitals.")
                    .howToApply("No registration required. Mobile health teams visit homes.")
                    .documentsRequired(List.of("Aadhaar Card"))
                    .officialWebsite("https://karunadu.karnataka.gov.in/hfw/").helplineNumber("104").launchDate("2024-02-01").build());

        if (!schemeRepo.existsById("SCH020"))
            schemeRepo.save(Scheme.builder().id("SCH020").name("Karnataka Arivu Education Loan Scheme").shortName("ARIVU")
                    .category("STUDENT").description("Subsidised education loan for SC/ST students in Karnataka for higher and professional education.")
                    .ministryDepartment("Karnataka SC/ST Development Corporation").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(35).gender("ALL").maxAnnualIncome(250000.0)
                    .eligibleCategories(List.of("SC","ST")).maritalStatus("ANY")
                    .benefitAmount(1000000.0).benefitType("ONE_TIME")
                    .benefitDescription("Loan up to Rs 10 lakh at 2% interest (SC), 0% for ST.")
                    .howToApply("Apply at Karnataka SC/ST Development Corporation district office or online.")
                    .documentsRequired(List.of("Aadhaar Card","Caste Certificate","Income Certificate","Admission Letter","Mark Sheets","Bank Details"))
                    .officialWebsite("https://kscstdc.karnataka.gov.in/").helplineNumber("080-22253370").launchDate("2013-01-01").build());

        if (!schemeRepo.existsById("SCH021"))
            schemeRepo.save(Scheme.builder().id("SCH021").name("Karnataka Swavalambi Sarathi Scheme").shortName("SARATHI")
                    .category("MINORITY").description("Vehicle subsidy scheme for minorities to buy auto-rickshaw, taxi or goods vehicle for livelihood.")
                    .ministryDepartment("Karnataka Minorities Development Corporation (KMDCL)").scope("STATE").applicableState("Karnataka")
                    .minAge(21).maxAge(55).gender("ALL").maxAnnualIncome(600000.0)
                    .eligibleCategories(List.of("MINORITY")).maritalStatus("ANY")
                    .benefitAmount(100000.0).benefitType("ONE_TIME")
                    .benefitDescription("Subsidy up to Rs 1 lakh on purchase of auto/taxi/goods vehicle.")
                    .howToApply("Apply online at KMDCL website with vehicle quotation and income proof.")
                    .documentsRequired(List.of("Aadhaar Card","Minority Community Certificate","Income Certificate","Bank Details","Driving Licence","Vehicle Quotation"))
                    .officialWebsite("https://kmdcl.karnataka.gov.in/").helplineNumber("080-22253500").launchDate("2025-01-01").build());

        if (!schemeRepo.existsById("SCH022"))
            schemeRepo.save(Scheme.builder().id("SCH022").name("Karnataka Janasevaka Scheme").shortName("JANASEVAKA")
                    .category("GOVERNANCE").description("Government services delivered at doorstep for citizens of Bengaluru Urban district.")
                    .ministryDepartment("BBMP / Bruhat Bengaluru Mahanagara Palike").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(120).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(0.0).benefitType("ONE_TIME")
                    .benefitDescription("Government certificates delivered at home within 7 days. Rs 65 service charge.")
                    .howToApply("Call 080-22660000 or visit bbmp.gov.in to book home visit.")
                    .documentsRequired(List.of("Aadhaar Card","Supporting documents as per certificate type"))
                    .officialWebsite("https://bbmp.gov.in/").helplineNumber("080-22660000").launchDate("2022-06-01").build());

        if (!schemeRepo.existsById("SCH023"))
            schemeRepo.save(Scheme.builder().id("SCH023").name("Karnataka Prabuddha Overseas Scholarship").shortName("PRABUDDHA")
                    .category("STUDENT").description("Full scholarship for SC students from Karnataka to pursue Masters or PhD abroad.")
                    .ministryDepartment("Karnataka SC/ST Welfare Department").scope("STATE").applicableState("Karnataka")
                    .minAge(21).maxAge(40).gender("ALL").maxAnnualIncome(800000.0)
                    .eligibleCategories(List.of("SC")).maritalStatus("ANY")
                    .benefitAmount(2500000.0).benefitType("YEARLY")
                    .benefitDescription("Full funding — tuition, travel, visa, living expenses for Masters/PhD at top 200 QS-ranked foreign universities.")
                    .howToApply("Apply during annual notification at Karnataka Scheduled Castes Development Corporation website.")
                    .documentsRequired(List.of("Aadhaar Card","Caste Certificate","Income Certificate","Admission Letter from Foreign University","Academic Transcripts"))
                    .officialWebsite("https://kscdc.net/").helplineNumber("080-22253370").launchDate("2020-01-01").build());

        if (!schemeRepo.existsById("SCH024"))
            schemeRepo.save(Scheme.builder().id("SCH024").name("Karnataka Ganga Kalyana Scheme").shortName("GANGA-KALYANA")
                    .category("FARMER").description("Borewell + pump set subsidy for small and marginal minority community farmers for irrigation.")
                    .ministryDepartment("Karnataka Minorities Development Corporation").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(65).gender("ALL").maxAnnualIncome(200000.0)
                    .eligibleCategories(List.of("MINORITY")).maritalStatus("ANY")
                    .benefitAmount(150000.0).benefitType("ONE_TIME")
                    .benefitDescription("Subsidy for borewell drilling + pump set installation. Up to Rs 1.5 lakh per farmer.")
                    .howToApply("Apply at KMDCL district office or online at kmdcl.karnataka.gov.in.")
                    .documentsRequired(List.of("Aadhaar Card","Minority Certificate","Land Records (RTC)","Income Certificate","Bank Details"))
                    .officialWebsite("https://kmdcl.karnataka.gov.in/").helplineNumber("080-22253500").launchDate("2015-01-01").build());

        if (!schemeRepo.existsById("SCH025"))
            schemeRepo.save(Scheme.builder().id("SCH025").name("Surya Ghar Muft Bijli Yojana (Solar Rooftop)").shortName("SURYA-GHAR")
                    .category("ELECTRICITY").description("Free rooftop solar panels for households to generate their own electricity and save on bills.")
                    .ministryDepartment("Ministry of New & Renewable Energy (MNRE)").scope("CENTRAL")
                    .minAge(18).maxAge(120).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(78000.0).benefitType("ONE_TIME")
                    .benefitDescription("Subsidy up to Rs 78,000 for 3 kW solar system. Generates 300 units/month free electricity.")
                    .howToApply("Apply at pmsuryaghar.gov.in, select empanelled vendor, get installation, then apply for subsidy.")
                    .documentsRequired(List.of("Aadhaar Card","Electricity Consumer Number","Bank Details","Property Documents"))
                    .officialWebsite("https://pmsuryaghar.gov.in/").helplineNumber("1800-180-3333").launchDate("2024-02-13").build());

        if (!schemeRepo.existsById("SCH026"))
            schemeRepo.save(Scheme.builder().id("SCH026").name("PM POSHAN Scheme (Mid-Day Meal)").shortName("PM-POSHAN")
                    .category("STUDENT").description("Free nutritious mid-day meals for children in government and government-aided schools.")
                    .ministryDepartment("Ministry of Education").scope("CENTRAL")
                    .minAge(5).maxAge(14).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("UNMARRIED").benefitAmount(0.0).benefitType("DAILY")
                    .benefitDescription("Free cooked meal every school day. Covers Classes 1-8 in government schools.")
                    .howToApply("Automatic for all students enrolled in government/aided schools.")
                    .documentsRequired(List.of("School Enrollment Proof"))
                    .officialWebsite("https://pmposhan.education.gov.in/").helplineNumber("1800-180-8004").launchDate("2021-09-29").build());

        if (!schemeRepo.existsById("SCH027"))
            schemeRepo.save(Scheme.builder().id("SCH027").name("Atal Pension Yojana").shortName("APY")
                    .category("ELDERLY").description("Guaranteed pension scheme for unorganised sector workers after age 60.")
                    .ministryDepartment("Ministry of Finance / PFRDA").scope("CENTRAL")
                    .minAge(18).maxAge(40).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(5000.0).benefitType("MONTHLY")
                    .benefitDescription("Guaranteed pension of Rs 1,000 to Rs 5,000/month from age 60, based on contribution level.")
                    .howToApply("Apply at any bank branch or post office savings account with Aadhaar and bank details.")
                    .documentsRequired(List.of("Aadhaar Card","Bank/Post Office Savings Account","Mobile Number"))
                    .officialWebsite("https://npscra.nsdl.co.in/scheme-details.php").helplineNumber("1800-110-069").launchDate("2015-05-09").build());

        if (!schemeRepo.existsById("SCH028"))
            schemeRepo.save(Scheme.builder().id("SCH028").name("Karnataka ST Marriage Assistance Scheme").shortName("ST-MARRIAGE")
                    .category("WOMEN").description("Financial assistance for marriage of Scheduled Tribe brides in Karnataka.")
                    .ministryDepartment("Karnataka ST Welfare Department").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(35).gender("FEMALE").maxAnnualIncome(200000.0)
                    .eligibleCategories(List.of("ST")).maritalStatus("UNMARRIED")
                    .benefitAmount(50000.0).benefitType("ONE_TIME")
                    .benefitDescription("Rs 50,000 financial assistance for marriage of ST bride.")
                    .howToApply("Apply at District SC/ST Welfare Office or via Seva Sindhu portal.")
                    .documentsRequired(List.of("Aadhaar Card","ST Caste Certificate","Marriage Invitation Card","Bride Bank Details","Income Certificate"))
                    .officialWebsite("https://stwelfare.karnataka.gov.in/").helplineNumber("080-22261789").launchDate("2010-01-01").build());

        if (!schemeRepo.existsById("SCH029"))
            schemeRepo.save(Scheme.builder().id("SCH029").name("Kisan Credit Card Scheme").shortName("KCC")
                    .category("FARMER").description("Short-term credit for farmers to meet agriculture, allied activities and consumption needs.")
                    .ministryDepartment("Ministry of Agriculture & Farmers Welfare / NABARD").scope("CENTRAL")
                    .minAge(18).maxAge(75).gender("ALL").eligibleCategories(List.of("BPL","APL","SC","ST","OBC","GENERAL"))
                    .maritalStatus("ANY").benefitAmount(300000.0).benefitType("ONE_TIME")
                    .benefitDescription("Credit up to Rs 3 lakh at 4% interest rate. Revolving credit for crop expenses.")
                    .howToApply("Apply at nearest bank branch with land and crop details.")
                    .documentsRequired(List.of("Aadhaar Card","Land Records (RTC)","Crop Details","Bank Account","Passport Photo"))
                    .officialWebsite("https://www.nabard.org/").helplineNumber("022-26539895").launchDate("1998-08-01").build());

        if (!schemeRepo.existsById("SCH030"))
            schemeRepo.save(Scheme.builder().id("SCH030").name("Karnataka SC Widow Re-Marriage Assistance Scheme").shortName("SC-WIDOW-REMARRIAGE")
                    .category("WOMEN").description("Financial assistance of Rs 3 lakh for widows from Scheduled Caste community who remarry in Karnataka.")
                    .ministryDepartment("Karnataka SC Welfare Department").scope("STATE").applicableState("Karnataka")
                    .minAge(18).maxAge(50).gender("FEMALE").maxAnnualIncome(200000.0)
                    .eligibleCategories(List.of("SC")).maritalStatus("WIDOW")
                    .benefitAmount(300000.0).benefitType("ONE_TIME")
                    .benefitDescription("Rs 3,00,000 one-time financial assistance for SC widow who remarries.")
                    .howToApply("Apply at District SC/ST Welfare Office within 1 year of remarriage.")
                    .documentsRequired(List.of("Aadhaar Card","First Husband's Death Certificate","SC Caste Certificate","Remarriage Certificate","Bank Details"))
                    .officialWebsite("https://www.karnataka.gov.in/").helplineNumber("080-22261789").launchDate("2012-01-01").build());

        log.info("Schemes seeded. Total in DB: {}", schemeRepo.count());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PROJECTS  — Across Karnataka districts (real ongoing projects 2024-25)
    // Total: 60 projects (PRJ001-PRJ060)
    // ─────────────────────────────────────────────────────────────────────────
    private void seedProjects() {
        log.info("Seeding projects (skipping existing)...");

        // ── EXISTING PROJECTS (PRJ001-PRJ008, Haveri district) ───────────────

        if (!projectRepo.existsById("PRJ001"))
            projectRepo.save(Project.builder().id("PRJ001").name("PMGSY Rural Road - Hanumanamatti to Hubli Main")
                    .category("ROAD").description("Construction of 5.2 km all-weather road under PMGSY.")
                    .location("Hanumanamatti").pincode("581115").district("Haveri").state("Karnataka")
                    .budgetAllocated(8500000.0).budgetSpent(5100000.0)
                    .status("IN_PROGRESS").completionPercentage(60)
                    .startDate("2024-06-15").expectedCompletionDate("2025-03-30")
                    .sanctioningAuthority("PMGSY - Ministry of Rural Development")
                    .contractor("M/s Shree Constructions Pvt Ltd").sourceLink("https://omms.nic.in/").build());

        if (!projectRepo.existsById("PRJ002"))
            projectRepo.save(Project.builder().id("PRJ002").name("Anganwadi Centre Construction - Hanumanamatti")
                    .category("WOMEN").description("New Anganwadi centre with kitchen, play area, and toilet facilities.")
                    .location("Hanumanamatti").pincode("581115").district("Haveri").state("Karnataka")
                    .budgetAllocated(1200000.0).budgetSpent(1200000.0)
                    .status("COMPLETED").completionPercentage(100)
                    .startDate("2023-08-10").expectedCompletionDate("2024-02-28")
                    .sanctioningAuthority("Ministry of Women & Child Development")
                    .contractor("Karnataka State Construction Dept").sourceLink("https://wcd.nic.in/").build());

        if (!projectRepo.existsById("PRJ003"))
            projectRepo.save(Project.builder().id("PRJ003").name("Primary Health Centre Upgrade - Hanumanamatti")
                    .category("HEALTH").description("Upgrading PHC with 10 beds and maternity ward.")
                    .location("Hanumanamatti").pincode("581115").district("Haveri").state("Karnataka")
                    .budgetAllocated(3500000.0).budgetSpent(800000.0)
                    .status("DELAYED").completionPercentage(23)
                    .startDate("2024-01-20").expectedCompletionDate("2024-10-31")
                    .sanctioningAuthority("National Health Mission")
                    .contractor("M/s Karnataka Health Infra").sourceLink("https://nhm.gov.in/").build());

        if (!projectRepo.existsById("PRJ004"))
            projectRepo.save(Project.builder().id("PRJ004").name("Jal Jeevan Mission - Tap Water - Hanumanamatti")
                    .category("WATER").description("Piped water supply to 450 households in the village.")
                    .location("Hanumanamatti").pincode("581115").district("Haveri").state("Karnataka")
                    .budgetAllocated(4500000.0).budgetSpent(3600000.0)
                    .status("IN_PROGRESS").completionPercentage(78)
                    .startDate("2024-04-01").expectedCompletionDate("2025-01-15")
                    .sanctioningAuthority("Jal Jeevan Mission").contractor("Hubli Water Works Pvt Ltd")
                    .sourceLink("https://jaljeevanmission.gov.in/").build());

        if (!projectRepo.existsById("PRJ005"))
            projectRepo.save(Project.builder().id("PRJ005").name("PMAY-G Housing - Hanumanamatti BPL Families")
                    .category("HOUSING").description("Construction of 45 pucca houses for BPL families.")
                    .location("Hanumanamatti").pincode("581115").district("Haveri").state("Karnataka")
                    .budgetAllocated(6750000.0).budgetSpent(4200000.0)
                    .status("IN_PROGRESS").completionPercentage(62)
                    .startDate("2024-02-15").expectedCompletionDate("2024-12-31")
                    .sanctioningAuthority("PMAY-G Ministry of Rural Development")
                    .contractor("Beneficiary-led construction").sourceLink("https://pmayg.nic.in/").build());

        if (!projectRepo.existsById("PRJ006"))
            projectRepo.save(Project.builder().id("PRJ006").name("Government Primary School Renovation - Hanumanamatti")
                    .category("SCHOOL").description("Renovation of school, new toilets, boundary wall, mid-day meal kitchen.")
                    .location("Hanumanamatti").pincode("581115").district("Haveri").state("Karnataka")
                    .budgetAllocated(1800000.0).budgetSpent(450000.0)
                    .status("IN_PROGRESS").completionPercentage(25)
                    .startDate("2024-07-01").expectedCompletionDate("2025-02-28")
                    .sanctioningAuthority("Samagra Shiksha - Ministry of Education")
                    .contractor("M/s Educate India Builders").sourceLink("https://samagra.education.gov.in/").build());

        if (!projectRepo.existsById("PRJ007"))
            projectRepo.save(Project.builder().id("PRJ007").name("Solar Street Lighting - Hanumanamatti")
                    .category("ELECTRICITY").description("Installation of 85 solar-powered LED street lights.")
                    .location("Hanumanamatti").pincode("581115").district("Haveri").state("Karnataka")
                    .budgetAllocated(1275000.0).budgetSpent(1275000.0)
                    .status("COMPLETED").completionPercentage(100)
                    .startDate("2024-03-10").expectedCompletionDate("2024-05-30")
                    .sanctioningAuthority("MNRE - Ministry of New & Renewable Energy")
                    .contractor("SolarTech Karnataka").sourceLink("https://mnre.gov.in/").build());

        if (!projectRepo.existsById("PRJ008"))
            projectRepo.save(Project.builder().id("PRJ008").name("MGNREGA Farm Pond Excavation - Hanumanamatti")
                    .category("AGRICULTURE").description("25 farm ponds for water harvesting under MGNREGA.")
                    .location("Hanumanamatti").pincode("581115").district("Haveri").state("Karnataka")
                    .budgetAllocated(1875000.0).budgetSpent(1500000.0)
                    .status("IN_PROGRESS").completionPercentage(80)
                    .startDate("2024-05-01").expectedCompletionDate("2024-11-30")
                    .sanctioningAuthority("MGNREGA - Ministry of Rural Development")
                    .contractor("Community labor - GP supervised").sourceLink("https://nrega.nic.in/").build());

        // ── Bengaluru Urban ──────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ009"))
            projectRepo.save(Project.builder().id("PRJ009").name("Namma Metro Phase 2A - Silk Board to KR Puram")
                    .category("TRANSPORT").description("14.5 km elevated metro corridor connecting IT hubs Silk Board to KR Puram via HSR Layout.")
                    .location("Bengaluru").pincode("560034").district("Bengaluru Urban").state("Karnataka")
                    .budgetAllocated(427000000000.0).budgetSpent(280000000000.0)
                    .status("IN_PROGRESS").completionPercentage(65)
                    .startDate("2020-01-01").expectedCompletionDate("2026-03-31")
                    .sanctioningAuthority("BMRCL / Ministry of Housing & Urban Affairs")
                    .contractor("Afcons Infrastructure Ltd").sourceLink("https://bmrc.co.in/").build());

        if (!projectRepo.existsById("PRJ010"))
            projectRepo.save(Project.builder().id("PRJ010").name("Bengaluru Outer Ring Road Elevated Corridor + Metro")
                    .category("TRANSPORT").description("32.15 km double-decker flyover with metro line along ORR to ease traffic congestion in Bengaluru.")
                    .location("Bengaluru").pincode("560037").district("Bengaluru Urban").state("Karnataka")
                    .budgetAllocated(1800000000000.0).budgetSpent(50000000000.0)
                    .status("PLANNING").completionPercentage(5)
                    .startDate("2025-01-01").expectedCompletionDate("2030-12-31")
                    .sanctioningAuthority("Karnataka Government / BBMP")
                    .contractor("TBD - Under Tender").sourceLink("https://bbmp.gov.in/").build());

        if (!projectRepo.existsById("PRJ011"))
            projectRepo.save(Project.builder().id("PRJ011").name("Bengaluru Smart City - Whitefield Area Development")
                    .category("URBAN").description("Integrated smart city infrastructure — roads, drainage, smart poles, free WiFi, CCTV in Whitefield.")
                    .location("Whitefield, Bengaluru").pincode("560066").district("Bengaluru Urban").state("Karnataka")
                    .budgetAllocated(2500000000.0).budgetSpent(1800000000.0)
                    .status("IN_PROGRESS").completionPercentage(72)
                    .startDate("2022-04-01").expectedCompletionDate("2025-09-30")
                    .sanctioningAuthority("Smart Cities Mission / BBMP / BESCOM")
                    .contractor("L&T Infrastructure Engineering").sourceLink("https://smartcities.gov.in/").build());

        if (!projectRepo.existsById("PRJ012"))
            projectRepo.save(Project.builder().id("PRJ012").name("Devanahalli Business Park (IT Park)")
                    .category("INDUSTRY").description("407-acre world-class IT and startup business park near Devanahalli, Bengaluru.")
                    .location("Devanahalli").pincode("562110").district("Bengaluru Rural").state("Karnataka")
                    .budgetAllocated(30000000000.0).budgetSpent(8000000000.0)
                    .status("IN_PROGRESS").completionPercentage(28)
                    .startDate("2022-09-01").expectedCompletionDate("2027-12-31")
                    .sanctioningAuthority("KIADB / Karnataka Industries Department")
                    .contractor("KIADB (Karnataka Industrial Areas Development Board)").sourceLink("https://kiadb.in/").build());

        // ── Mysuru District ──────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ013"))
            projectRepo.save(Project.builder().id("PRJ013").name("Mysuru-Kushalnagar NH-275 4-Lane Widening")
                    .category("ROAD").description("Widening of NH-275 to 4 lanes from Mysuru to Kushalnagar for 85 km to improve connectivity to Kodagu.")
                    .location("Mysuru").pincode("570001").district("Mysuru").state("Karnataka")
                    .budgetAllocated(32000000000.0).budgetSpent(12000000000.0)
                    .status("IN_PROGRESS").completionPercentage(38)
                    .startDate("2023-03-15").expectedCompletionDate("2026-09-30")
                    .sanctioningAuthority("NHAI / Ministry of Road Transport & Highways")
                    .contractor("GR Infraprojects Ltd").sourceLink("https://nhai.gov.in/").build());

        if (!projectRepo.existsById("PRJ014"))
            projectRepo.save(Project.builder().id("PRJ014").name("Mysuru New Town Water Supply Scheme")
                    .category("WATER").description("New water treatment plant and pipeline network to supply 24x7 safe drinking water to Mysuru city.")
                    .location("Mysuru").pincode("570001").district("Mysuru").state("Karnataka")
                    .budgetAllocated(4200000000.0).budgetSpent(2100000000.0)
                    .status("IN_PROGRESS").completionPercentage(50)
                    .startDate("2023-07-01").expectedCompletionDate("2025-12-31")
                    .sanctioningAuthority("AMRUT 2.0 / Karnataka Urban Water Supply & Drainage Board")
                    .contractor("SPML Infra Ltd").sourceLink("https://amrut.gov.in/").build());

        if (!projectRepo.existsById("PRJ015"))
            projectRepo.save(Project.builder().id("PRJ015").name("Mysuru Government Medical College Hospital Upgrade")
                    .category("HEALTH").description("Expansion of MMC&RI hospital — new 500-bed block, ICU, trauma centre and modern operation theatres.")
                    .location("Mysuru").pincode("570001").district("Mysuru").state("Karnataka")
                    .budgetAllocated(8000000000.0).budgetSpent(2500000000.0)
                    .status("IN_PROGRESS").completionPercentage(31)
                    .startDate("2024-01-15").expectedCompletionDate("2027-06-30")
                    .sanctioningAuthority("National Health Mission / Karnataka Health Dept")
                    .contractor("Shapoorji Pallonji Construction").sourceLink("https://nhm.gov.in/").build());

        // ── Shivamogga District ──────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ016"))
            projectRepo.save(Project.builder().id("PRJ016").name("Sharavathi Bridge & NH Projects - Shivamogga")
                    .category("ROAD").description("9 NH projects including new Sharavathi Bridge to improve connectivity between Malnad and coastal regions.")
                    .location("Sagara, Shivamogga").pincode("577401").district("Shivamogga").state("Karnataka")
                    .budgetAllocated(2000000000.0).budgetSpent(1900000000.0)
                    .status("COMPLETED").completionPercentage(100)
                    .startDate("2022-04-01").expectedCompletionDate("2025-07-14")
                    .sanctioningAuthority("NHAI / Ministry of Road Transport & Highways")
                    .contractor("KCVR Infra Projects Pvt Ltd").sourceLink("https://nhai.gov.in/").build());

        if (!projectRepo.existsById("PRJ017"))
            projectRepo.save(Project.builder().id("PRJ017").name("Shivamogga Airport Terminal Expansion")
                    .category("TRANSPORT").description("Expansion of Shivamogga Airport terminal to handle increased passenger traffic with new runway and facilities.")
                    .location("Shivamogga").pincode("577201").district("Shivamogga").state("Karnataka")
                    .budgetAllocated(5000000000.0).budgetSpent(1200000000.0)
                    .status("IN_PROGRESS").completionPercentage(24)
                    .startDate("2024-04-01").expectedCompletionDate("2026-12-31")
                    .sanctioningAuthority("AAI / Ministry of Civil Aviation")
                    .contractor("KMB Infra Ltd").sourceLink("https://aai.aero/").build());

        // ── Dharwad District ─────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ018"))
            projectRepo.save(Project.builder().id("PRJ018").name("Dharwad Smart City - City Core Redevelopment")
                    .category("URBAN").description("Redevelopment of Dharwad city core — underground cabling, heritage walks, storm water drains, smart parking.")
                    .location("Dharwad").pincode("580001").district("Dharwad").state("Karnataka")
                    .budgetAllocated(1800000000.0).budgetSpent(900000000.0)
                    .status("IN_PROGRESS").completionPercentage(50)
                    .startDate("2022-11-01").expectedCompletionDate("2025-10-31")
                    .sanctioningAuthority("Smart Cities Mission / Dharwad City Corporation")
                    .contractor("KEONICS / Uday Constructions").sourceLink("https://smartcities.gov.in/").build());

        if (!projectRepo.existsById("PRJ019"))
            projectRepo.save(Project.builder().id("PRJ019").name("Smart Classroom Setup - Govt Schools Dharwad")
                    .category("SCHOOL").description("Installation of smart boards, projectors, computers and internet in 120 government schools in Dharwad district.")
                    .location("Dharwad").pincode("580001").district("Dharwad").state("Karnataka")
                    .budgetAllocated(240000000.0).budgetSpent(90000000.0)
                    .status("IN_PROGRESS").completionPercentage(38)
                    .startDate("2025-01-10").expectedCompletionDate("2025-08-31")
                    .sanctioningAuthority("Samagra Shiksha - Ministry of Education")
                    .contractor("DigiLearn Systems Pvt Ltd").sourceLink("https://samagra.education.gov.in/").build());

        // ── Dakshina Kannada District ─────────────────────────────────────────

        if (!projectRepo.existsById("PRJ020"))
            projectRepo.save(Project.builder().id("PRJ020").name("Shiradi Ghat NH-75 Restoration Works")
                    .category("ROAD").description("Restoration and slope protection works on NH-75 Shiradi Ghat stretch for monsoon safety.")
                    .location("Shiradi Ghat").pincode("574328").district("Dakshina Kannada").state("Karnataka")
                    .budgetAllocated(3500000000.0).budgetSpent(2800000000.0)
                    .status("IN_PROGRESS").completionPercentage(80)
                    .startDate("2023-10-01").expectedCompletionDate("2025-06-30")
                    .sanctioningAuthority("NHAI / Ministry of Road Transport & Highways")
                    .contractor("G R Infraprojects Ltd").sourceLink("https://nhai.gov.in/").build());

        if (!projectRepo.existsById("PRJ021"))
            projectRepo.save(Project.builder().id("PRJ021").name("New Mangaluru Port Capacity Expansion")
                    .category("INDUSTRY").description("Expansion of New Mangaluru Port — new berths, mechanised coal handling plant, deep draft facility.")
                    .location("Mangaluru").pincode("575011").district("Dakshina Kannada").state("Karnataka")
                    .budgetAllocated(12000000000.0).budgetSpent(4000000000.0)
                    .status("IN_PROGRESS").completionPercentage(33)
                    .startDate("2023-06-01").expectedCompletionDate("2027-03-31")
                    .sanctioningAuthority("Ministry of Ports, Shipping & Waterways")
                    .contractor("Adani Ports & Special Economic Zone").sourceLink("https://nmpt.gov.in/").build());

        // ── Bidar District ───────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ022"))
            projectRepo.save(Project.builder().id("PRJ022").name("Bidar-Humnabad NH-367 4-Lane Widening")
                    .category("ROAD").description("Widening of 47 km Bidar-Humnabad section of NH-367 to 4 lanes.")
                    .location("Bidar").pincode("585401").district("Bidar").state("Karnataka")
                    .budgetAllocated(8500000000.0).budgetSpent(1200000000.0)
                    .status("IN_PROGRESS").completionPercentage(14)
                    .startDate("2025-01-01").expectedCompletionDate("2027-06-30")
                    .sanctioningAuthority("NHAI / Ministry of Road Transport & Highways")
                    .contractor("SRC Infra Developers Pvt Ltd").sourceLink("https://nhai.gov.in/").build());

        if (!projectRepo.existsById("PRJ023"))
            projectRepo.save(Project.builder().id("PRJ023").name("Bidar Jal Jeevan Mission - Rural Water Supply")
                    .category("WATER").description("Piped water connections to 89,000 rural households in Bidar district under Jal Jeevan Mission.")
                    .location("Bidar").pincode("585401").district("Bidar").state("Karnataka")
                    .budgetAllocated(9800000000.0).budgetSpent(6500000000.0)
                    .status("IN_PROGRESS").completionPercentage(66)
                    .startDate("2022-08-01").expectedCompletionDate("2025-09-30")
                    .sanctioningAuthority("Jal Jeevan Mission / Karnataka Rural Water Supply Dept")
                    .contractor("VA Tech Wabag Ltd").sourceLink("https://jaljeevanmission.gov.in/").build());

        // ── Kalaburagi District ──────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ024"))
            projectRepo.save(Project.builder().id("PRJ024").name("Kalaburagi AIIMS - All India Institute of Medical Sciences")
                    .category("HEALTH").description("New AIIMS hospital campus with 750 beds, medical college, nursing college and research facilities for North Karnataka.")
                    .location("Kalaburagi").pincode("585102").district("Kalaburagi").state("Karnataka")
                    .budgetAllocated(160000000000.0).budgetSpent(40000000000.0)
                    .status("IN_PROGRESS").completionPercentage(25)
                    .startDate("2022-01-15").expectedCompletionDate("2027-03-31")
                    .sanctioningAuthority("Ministry of Health & Family Welfare")
                    .contractor("Shapoorji Pallonji & Company").sourceLink("https://aiimskalyani.edu.in/").build());

        if (!projectRepo.existsById("PRJ025"))
            projectRepo.save(Project.builder().id("PRJ025").name("Kalaburagi District Irrigation - Upper Krishna Project")
                    .category("AGRICULTURE").description("Canal network completion under Upper Krishna Project Phase-III to irrigate 2.13 lakh acres in Kalaburagi district.")
                    .location("Kalaburagi").pincode("585101").district("Kalaburagi").state("Karnataka")
                    .budgetAllocated(48000000000.0).budgetSpent(28000000000.0)
                    .status("IN_PROGRESS").completionPercentage(58)
                    .startDate("2020-04-01").expectedCompletionDate("2026-03-31")
                    .sanctioningAuthority("Karnataka Neeravari Nigama Ltd / Jal Shakti Ministry")
                    .contractor("KNNL + Multiple Contractors").sourceLink("https://knnl.karnataka.gov.in/").build());

        // ── Belagavi District ────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ026"))
            projectRepo.save(Project.builder().id("PRJ026").name("Belagavi-Pune NH-48 Expressway (Bangalore-Pune Expressway)")
                    .category("ROAD").description("6-lane access-controlled expressway connecting Bengaluru to Pune via Belagavi under Bharatmala Phase-II.")
                    .location("Belagavi").pincode("590001").district("Belagavi").state("Karnataka")
                    .budgetAllocated(193200000000.0).budgetSpent(30000000000.0)
                    .status("IN_PROGRESS").completionPercentage(16)
                    .startDate("2023-06-01").expectedCompletionDate("2028-12-31")
                    .sanctioningAuthority("NHAI - Bharatmala Pariyojana Phase-II")
                    .contractor("Multiple contractors - Packagewise").sourceLink("https://nhai.gov.in/").build());

        if (!projectRepo.existsById("PRJ027"))
            projectRepo.save(Project.builder().id("PRJ027").name("Belagavi Smart City - Central Business District")
                    .category("URBAN").description("Redevelopment of Belagavi CBD with underground utilities, heritage precincts, multi-level parking, smart surveillance.")
                    .location("Belagavi").pincode("590001").district("Belagavi").state("Karnataka")
                    .budgetAllocated(1600000000.0).budgetSpent(700000000.0)
                    .status("IN_PROGRESS").completionPercentage(44)
                    .startDate("2023-01-01").expectedCompletionDate("2026-03-31")
                    .sanctioningAuthority("Smart Cities Mission / Belagavi City Corporation")
                    .contractor("L&T Construction").sourceLink("https://smartcities.gov.in/").build());

        // ── Tumkuru District ─────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ028"))
            projectRepo.save(Project.builder().id("PRJ028").name("Tumkuru Industrial Township (TUDA)")
                    .category("INDUSTRY").description("2000-acre integrated industrial township near Tumkuru on NH-48 corridor with plug-and-play infrastructure.")
                    .location("Tumkuru").pincode("572101").district("Tumkuru").state("Karnataka")
                    .budgetAllocated(50000000000.0).budgetSpent(15000000000.0)
                    .status("IN_PROGRESS").completionPercentage(30)
                    .startDate("2021-09-01").expectedCompletionDate("2026-12-31")
                    .sanctioningAuthority("KIADB / DPIIT / National Industrial Corridor Prog")
                    .contractor("KIADB + Shapoorji Pallonji").sourceLink("https://kiadb.in/").build());

        if (!projectRepo.existsById("PRJ029"))
            projectRepo.save(Project.builder().id("PRJ029").name("Tumkuru Jal Jeevan Mission - Urban Water Supply")
                    .category("WATER").description("24x7 piped water supply to all 1.2 lakh households in Tumkuru city under AMRUT 2.0.")
                    .location("Tumkuru").pincode("572101").district("Tumkuru").state("Karnataka")
                    .budgetAllocated(5200000000.0).budgetSpent(2900000000.0)
                    .status("IN_PROGRESS").completionPercentage(55)
                    .startDate("2023-04-01").expectedCompletionDate("2025-12-31")
                    .sanctioningAuthority("AMRUT 2.0 / KUWSDB")
                    .contractor("SPML Infra Ltd").sourceLink("https://amrut.gov.in/").build());

        // ── Kolar District ───────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ030"))
            projectRepo.save(Project.builder().id("PRJ030").name("Bengaluru-Chennai Expressway (KGF to Hoskote Operational)")
                    .category("ROAD").description("6-lane access-controlled expressway. 68 km Hoskote-KGF section operational. Full 262 km route under progress.")
                    .location("Bengaluru / Kolar").pincode("563118").district("Kolar").state("Karnataka")
                    .budgetAllocated(173000000000.0).budgetSpent(120000000000.0)
                    .status("IN_PROGRESS").completionPercentage(70)
                    .startDate("2020-07-01").expectedCompletionDate("2026-03-31")
                    .sanctioningAuthority("NHAI / Ministry of Road Transport & Highways")
                    .contractor("Dilip Buildcon + PNC Infratech").sourceLink("https://nhai.gov.in/").build());

        // ── Vijayapura District ──────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ031"))
            projectRepo.save(Project.builder().id("PRJ031").name("Vijayapura District Hospital Modernisation")
                    .category("HEALTH").description("Modernisation of Vijayapura District Government Hospital with new OT complex, ICU, NICU, lab and emergency block.")
                    .location("Vijayapura").pincode("586101").district("Vijayapura").state("Karnataka")
                    .budgetAllocated(2800000000.0).budgetSpent(800000000.0)
                    .status("IN_PROGRESS").completionPercentage(29)
                    .startDate("2024-03-01").expectedCompletionDate("2026-06-30")
                    .sanctioningAuthority("NHM / Karnataka Health & Family Welfare Dept")
                    .contractor("B.G. Shirke Construction Technology").sourceLink("https://nhm.gov.in/").build());

        if (!projectRepo.existsById("PRJ032"))
            projectRepo.save(Project.builder().id("PRJ032").name("Vijayapura Urban Flood Mitigation - Storm Water Drains")
                    .category("WATER").description("Construction of 48 km storm water drains and retention ponds to prevent urban flooding in Vijayapura city.")
                    .location("Vijayapura").pincode("586101").district("Vijayapura").state("Karnataka")
                    .budgetAllocated(1500000000.0).budgetSpent(600000000.0)
                    .status("IN_PROGRESS").completionPercentage(40)
                    .startDate("2024-06-01").expectedCompletionDate("2026-03-31")
                    .sanctioningAuthority("AMRUT 2.0 / Karnataka Urban Development Dept")
                    .contractor("Uday Constructions Pvt Ltd").sourceLink("https://amrut.gov.in/").build());

        // ── Raichur District ─────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ033"))
            projectRepo.save(Project.builder().id("PRJ033").name("Raichur RTPS Solar Park Extension")
                    .category("ELECTRICITY").description("500 MW solar power park extension at Raichur Thermal Power Station site to transition to renewable energy.")
                    .location("Raichur").pincode("584101").district("Raichur").state("Karnataka")
                    .budgetAllocated(25000000000.0).budgetSpent(5000000000.0)
                    .status("IN_PROGRESS").completionPercentage(20)
                    .startDate("2024-09-01").expectedCompletionDate("2027-03-31")
                    .sanctioningAuthority("MNRE / KREDL / KPCL")
                    .contractor("Adani Green Energy / KPCL").sourceLink("https://kredl.kar.nic.in/").build());

        if (!projectRepo.existsById("PRJ034"))
            projectRepo.save(Project.builder().id("PRJ034").name("Raichur Narayanpura Canal Irrigation Revival")
                    .category("AGRICULTURE").description("Restoration and lining of 320 km canal network from Narayanpura dam to irrigate 1.5 lakh acres in Raichur.")
                    .location("Raichur").pincode("584101").district("Raichur").state("Karnataka")
                    .budgetAllocated(12000000000.0).budgetSpent(7200000000.0)
                    .status("IN_PROGRESS").completionPercentage(60)
                    .startDate("2022-10-01").expectedCompletionDate("2025-09-30")
                    .sanctioningAuthority("Karnataka Neeravari Nigama Ltd")
                    .contractor("KNNL Direct").sourceLink("https://knnl.karnataka.gov.in/").build());

        // ── Kodagu District ──────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ035"))
            projectRepo.save(Project.builder().id("PRJ035").name("Kodagu Flood Rehabilitation - Houses & Roads")
                    .category("HOUSING").description("Post-flood reconstruction of 2,500 houses and 180 km damaged rural roads in Kodagu under NDRF/SDRF.")
                    .location("Madikeri, Kodagu").pincode("571201").district("Kodagu").state("Karnataka")
                    .budgetAllocated(8500000000.0).budgetSpent(5000000000.0)
                    .status("IN_PROGRESS").completionPercentage(58)
                    .startDate("2022-03-01").expectedCompletionDate("2025-12-31")
                    .sanctioningAuthority("NDRF / SDRF / Karnataka Revenue Department")
                    .contractor("Multiple contractors + KPWD").sourceLink("https://ksdma.karnataka.gov.in/").build());

        // ── Hassan District ──────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ036"))
            projectRepo.save(Project.builder().id("PRJ036").name("Hassan District Solar-Powered Irrigation Pumps (KUSUM)")
                    .category("AGRICULTURE").description("Installation of 8,000 solar pumps for farmers in Hassan district under PM-KUSUM scheme to replace diesel pumps.")
                    .location("Hassan").pincode("573201").district("Hassan").state("Karnataka")
                    .budgetAllocated(4000000000.0).budgetSpent(2400000000.0)
                    .status("IN_PROGRESS").completionPercentage(60)
                    .startDate("2023-06-01").expectedCompletionDate("2025-09-30")
                    .sanctioningAuthority("MNRE / PM-KUSUM / KREDL")
                    .contractor("Vikram Solar / KPCL empanelled vendors").sourceLink("https://pmkusum.mnre.gov.in/").build());

        // ── Chikkamagaluru District ───────────────────────────────────────────

        if (!projectRepo.existsById("PRJ037"))
            projectRepo.save(Project.builder().id("PRJ037").name("Chikkamagaluru Eco-Tourism Circuit Development")
                    .category("TOURISM").description("Development of eco-tourism circuits linking Mullayanagiri, Kudremukh, Bhadra Wildlife — new trails, rest houses, visitor centres.")
                    .location("Chikkamagaluru").pincode("577101").district("Chikkamagaluru").state("Karnataka")
                    .budgetAllocated(800000000.0).budgetSpent(200000000.0)
                    .status("IN_PROGRESS").completionPercentage(25)
                    .startDate("2024-07-01").expectedCompletionDate("2026-06-30")
                    .sanctioningAuthority("Karnataka Tourism Dept / PRASAD Scheme / Ministry of Tourism")
                    .contractor("Karnataka State Tourism Development Corp").sourceLink("https://karnatakatourism.org/").build());

        // ── Udupi District ───────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ038"))
            projectRepo.save(Project.builder().id("PRJ038").name("Udupi Kundapur Coastal Road Development")
                    .category("ROAD").description("4-lane coastal highway development from Udupi to Kundapur with sea bridges and bypasses under Sagarmala Project.")
                    .location("Udupi").pincode("576101").district("Udupi").state("Karnataka")
                    .budgetAllocated(12000000000.0).budgetSpent(3500000000.0)
                    .status("IN_PROGRESS").completionPercentage(29)
                    .startDate("2023-09-01").expectedCompletionDate("2027-03-31")
                    .sanctioningAuthority("Sagarmala / NHAI / MoRTH")
                    .contractor("G R Infraprojects + Coastal Road Corp").sourceLink("https://sagarmala.gov.in/").build());

        // ── Ballari District ─────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ039"))
            projectRepo.save(Project.builder().id("PRJ039").name("Ballari Steel Plant Township Housing - SAIL")
                    .category("HOUSING").description("Construction of 3,200 affordable housing units for SAIL Ballari plant workers and BPL families under PMAY.")
                    .location("Ballari").pincode("583101").district("Ballari").state("Karnataka")
                    .budgetAllocated(6500000000.0).budgetSpent(3200000000.0)
                    .status("IN_PROGRESS").completionPercentage(49)
                    .startDate("2022-11-01").expectedCompletionDate("2025-11-30")
                    .sanctioningAuthority("PMAY / SAIL / Ministry of Steel")
                    .contractor("NBCC India Ltd").sourceLink("https://pmay-urban.gov.in/").build());

        // ── Gadag District ───────────────────────────────────────────────────

        if (!projectRepo.existsById("PRJ040"))
            projectRepo.save(Project.builder().id("PRJ040").name("Gadag Wind Energy Expansion - Kappatagudda")
                    .category("ELECTRICITY").description("300 MW wind power project at Kappatagudda hills in Gadag district to boost Karnataka's renewable energy capacity.")
                    .location("Gadag").pincode("582101").district("Gadag").state("Karnataka")
                    .budgetAllocated(18000000000.0).budgetSpent(4000000000.0)
                    .status("IN_PROGRESS").completionPercentage(22)
                    .startDate("2024-11-01").expectedCompletionDate("2027-06-30")
                    .sanctioningAuthority("MNRE / KREDL / Ministry of Power")
                    .contractor("Greenko Energy Holdings Pvt Ltd").sourceLink("https://kredl.kar.nic.in/").build());

        // ── NEW PROJECTS (PRJ041-PRJ060) ──────────────────────────────────────

        // Chitradurga District
        if (!projectRepo.existsById("PRJ041"))
            projectRepo.save(Project.builder().id("PRJ041").name("Vanivilas Reservoir Modernisation - Chitradurga")
                    .category("WATER").description("Modernisation and desilting of Vanivilas Sagar reservoir with improved canal network to irrigate 1.8 lakh acres in Chitradurga and Davanagere districts.")
                    .location("Chitradurga").pincode("577501").district("Chitradurga").state("Karnataka")
                    .budgetAllocated(6500000000.0).budgetSpent(2800000000.0)
                    .status("IN_PROGRESS").completionPercentage(43)
                    .startDate("2023-05-01").expectedCompletionDate("2026-03-31")
                    .sanctioningAuthority("Karnataka Neeravari Nigama Ltd / Jal Shakti Ministry")
                    .contractor("KNNL + Navayuga Engineering Company").sourceLink("https://knnl.karnataka.gov.in/").build());

        if (!projectRepo.existsById("PRJ042"))
            projectRepo.save(Project.builder().id("PRJ042").name("Chitradurga Fort Heritage Conservation & Tourism")
                    .category("TOURISM").description("Conservation and restoration of the 17th-century Chitradurga Fort with visitor amenities, light & sound show and heritage museum.")
                    .location("Chitradurga").pincode("577501").district("Chitradurga").state("Karnataka")
                    .budgetAllocated(450000000.0).budgetSpent(180000000.0)
                    .status("IN_PROGRESS").completionPercentage(40)
                    .startDate("2023-11-01").expectedCompletionDate("2025-10-31")
                    .sanctioningAuthority("Archaeological Survey of India / Karnataka Tourism Dept")
                    .contractor("Karnataka State Tourism Development Corp").sourceLink("https://asi.nic.in/").build());

        // Davanagere District
        if (!projectRepo.existsById("PRJ043"))
            projectRepo.save(Project.builder().id("PRJ043").name("Davanagere Textile Park - Integrated Apparel Cluster")
                    .category("INDUSTRY").description("300-acre integrated textile and apparel manufacturing cluster at Davanagere to generate 50,000+ jobs in handloom and power-loom sector.")
                    .location("Davanagere").pincode("577001").district("Davanagere").state("Karnataka")
                    .budgetAllocated(8000000000.0).budgetSpent(2000000000.0)
                    .status("IN_PROGRESS").completionPercentage(25)
                    .startDate("2024-03-01").expectedCompletionDate("2027-09-30")
                    .sanctioningAuthority("Ministry of Textiles / KIADB / Karnataka Industries Dept")
                    .contractor("KIADB + Arvind Infrastructure Ltd").sourceLink("https://kiadb.in/").build());

        if (!projectRepo.existsById("PRJ044"))
            projectRepo.save(Project.builder().id("PRJ044").name("Davanagere AMRUT 2.0 - Sewage Treatment Plant")
                    .category("WATER").description("New 60 MLD sewage treatment plant and underground sewerage network covering all 35 wards of Davanagere city.")
                    .location("Davanagere").pincode("577001").district("Davanagere").state("Karnataka")
                    .budgetAllocated(3200000000.0).budgetSpent(960000000.0)
                    .status("IN_PROGRESS").completionPercentage(30)
                    .startDate("2024-02-01").expectedCompletionDate("2026-08-31")
                    .sanctioningAuthority("AMRUT 2.0 / Karnataka Urban Development Dept")
                    .contractor("VA Tech Wabag Ltd").sourceLink("https://amrut.gov.in/").build());

        // Koppal District
        if (!projectRepo.existsById("PRJ045"))
            projectRepo.save(Project.builder().id("PRJ045").name("Koppal Solar Energy Park - 2000 MW")
                    .category("ELECTRICITY").description("Ultra Mega Solar Power Park at Shapur, Koppal — one of the largest in Karnataka, feeding into the national grid.")
                    .location("Shapur, Koppal").pincode("583231").district("Koppal").state("Karnataka")
                    .budgetAllocated(100000000000.0).budgetSpent(35000000000.0)
                    .status("IN_PROGRESS").completionPercentage(35)
                    .startDate("2022-07-01").expectedCompletionDate("2026-12-31")
                    .sanctioningAuthority("MNRE / KREDL / SECI")
                    .contractor("Adani Green Energy + ReNew Power").sourceLink("https://kredl.kar.nic.in/").build());

        if (!projectRepo.existsById("PRJ046"))
            projectRepo.save(Project.builder().id("PRJ046").name("Koppal District Hospital Expansion")
                    .category("HEALTH").description("New 300-bed block with ICU, dialysis unit, blood bank and modern diagnostic centre at Koppal District Government Hospital.")
                    .location("Koppal").pincode("583231").district("Koppal").state("Karnataka")
                    .budgetAllocated(1800000000.0).budgetSpent(450000000.0)
                    .status("IN_PROGRESS").completionPercentage(25)
                    .startDate("2024-06-01").expectedCompletionDate("2026-12-31")
                    .sanctioningAuthority("NHM / Karnataka Health & Family Welfare Dept")
                    .contractor("Ahluwalia Contracts India Ltd").sourceLink("https://nhm.gov.in/").build());

        // Yadgir District
        if (!projectRepo.existsById("PRJ047"))
            projectRepo.save(Project.builder().id("PRJ047").name("Yadgir Jal Jeevan Mission - Multi Village Water Supply")
                    .category("WATER").description("Multi-village rural water supply scheme covering 320 villages in Yadgir district with piped water from River Krishna.")
                    .location("Yadgir").pincode("585201").district("Yadgir").state("Karnataka")
                    .budgetAllocated(12500000000.0).budgetSpent(7500000000.0)
                    .status("IN_PROGRESS").completionPercentage(60)
                    .startDate("2022-04-01").expectedCompletionDate("2025-12-31")
                    .sanctioningAuthority("Jal Jeevan Mission / Karnataka Rural Water Supply Dept")
                    .contractor("Megha Engineering & Infrastructures Ltd").sourceLink("https://jaljeevanmission.gov.in/").build());

        if (!projectRepo.existsById("PRJ048"))
            projectRepo.save(Project.builder().id("PRJ048").name("Yadgir Model Residential School (Morarji Desai)")
                    .category("SCHOOL").description("Construction of new Morarji Desai Residential School campus with 500 student capacity, labs, library, sports facilities for SC/ST students.")
                    .location("Yadgir").pincode("585201").district("Yadgir").state("Karnataka")
                    .budgetAllocated(600000000.0).budgetSpent(420000000.0)
                    .status("IN_PROGRESS").completionPercentage(70)
                    .startDate("2023-09-01").expectedCompletionDate("2025-06-30")
                    .sanctioningAuthority("Karnataka SC/ST Welfare Dept / Ekalavya Model School Scheme")
                    .contractor("Karnataka State Construction Corp").sourceLink("https://sswkarnataka.ac.in/").build());

        // Chamarajanagar District
        if (!projectRepo.existsById("PRJ049"))
            projectRepo.save(Project.builder().id("PRJ049").name("Chamarajanagar PMAY-G Tribal Housing - Soliga Community")
                    .category("HOUSING").description("Construction of 3,800 pucca houses for Soliga and other forest-dwelling tribal communities in Chamarajanagar under PMAY-G.")
                    .location("Chamarajanagar").pincode("571313").district("Chamarajanagar").state("Karnataka")
                    .budgetAllocated(5700000000.0).budgetSpent(3420000000.0)
                    .status("IN_PROGRESS").completionPercentage(60)
                    .startDate("2022-10-01").expectedCompletionDate("2025-09-30")
                    .sanctioningAuthority("PMAY-G / Ministry of Tribal Affairs / Ministry of Rural Development")
                    .contractor("Beneficiary-led construction supervised by GP").sourceLink("https://pmayg.nic.in/").build());

        if (!projectRepo.existsById("PRJ050"))
            projectRepo.save(Project.builder().id("PRJ050").name("Biligirirangaswamy Temple Wildlife Sanctuary - Eco-Tourism")
                    .category("TOURISM").description("Development of sustainable eco-tourism infrastructure at BRT Wildlife Sanctuary with nature trails, bamboo cottages and tribal cultural centre.")
                    .location("Chamarajanagar").pincode("571441").district("Chamarajanagar").state("Karnataka")
                    .budgetAllocated(350000000.0).budgetSpent(105000000.0)
                    .status("IN_PROGRESS").completionPercentage(30)
                    .startDate("2024-04-01").expectedCompletionDate("2026-03-31")
                    .sanctioningAuthority("Karnataka Forest Dept / Karnataka Tourism Dept")
                    .contractor("Karnataka State Forest Development Corp").sourceLink("https://karnatakatourism.org/").build());

        // Mandya District
        if (!projectRepo.existsById("PRJ051"))
            projectRepo.save(Project.builder().id("PRJ051").name("KRS Dam Strengthening & Cauvery Water Supply Improvement")
                    .category("WATER").description("Structural strengthening of Krishnarajasagara Dam and modernisation of water distribution network for Mandya, Mysuru and Bengaluru.")
                    .location("Mandya").pincode("571401").district("Mandya").state("Karnataka")
                    .budgetAllocated(7500000000.0).budgetSpent(3000000000.0)
                    .status("IN_PROGRESS").completionPercentage(40)
                    .startDate("2023-08-01").expectedCompletionDate("2026-09-30")
                    .sanctioningAuthority("Karnataka Cauvery Neeravari Nigama / Jal Shakti")
                    .contractor("Navayuga Engineering Co + KNNL").sourceLink("https://knnl.karnataka.gov.in/").build());

        if (!projectRepo.existsById("PRJ052"))
            projectRepo.save(Project.builder().id("PRJ052").name("Mandya Sugar Industry Modernisation - MYSORE SUGAR")
                    .category("INDUSTRY").description("Expansion and modernisation of Mysore Sugar Company at Mandya — new 5,000 TCD plant, ethanol distillery and co-generation power plant.")
                    .location("Mandya").pincode("571401").district("Mandya").state("Karnataka")
                    .budgetAllocated(3500000000.0).budgetSpent(1750000000.0)
                    .status("IN_PROGRESS").completionPercentage(50)
                    .startDate("2023-11-01").expectedCompletionDate("2025-10-31")
                    .sanctioningAuthority("Karnataka Cooperative Societies Dept / Ministry of Food Processing")
                    .contractor("Triveni Engineering & Industries Ltd").sourceLink("https://www.karnataka.gov.in/").build());

        // Bagalkot District
        if (!projectRepo.existsById("PRJ053"))
            projectRepo.save(Project.builder().id("PRJ053").name("Almatti Dam Backwater Area Irrigation Expansion")
                    .category("AGRICULTURE").description("Extension of irrigation canals from Almatti Reservoir to bring additional 3 lakh acres under irrigation in Bagalkot, Vijayapura and Raichur districts.")
                    .location("Bagalkot").pincode("587101").district("Bagalkot").state("Karnataka")
                    .budgetAllocated(38000000000.0).budgetSpent(15200000000.0)
                    .status("IN_PROGRESS").completionPercentage(40)
                    .startDate("2021-06-01").expectedCompletionDate("2026-06-30")
                    .sanctioningAuthority("Upper Krishna Project Authority / KNNL / Jal Shakti")
                    .contractor("KNNL + Ramky Infrastructure").sourceLink("https://knnl.karnataka.gov.in/").build());

        if (!projectRepo.existsById("PRJ054"))
            projectRepo.save(Project.builder().id("PRJ054").name("Badami-Pattadakal-Aihole Heritage Corridor")
                    .category("TOURISM").description("Integrated heritage tourism corridor connecting Badami, Pattadakal and Aihole — UNESCO World Heritage sites — with new roads, museums, rest areas and digital interpretation centres.")
                    .location("Badami, Bagalkot").pincode("587201").district("Bagalkot").state("Karnataka")
                    .budgetAllocated(1200000000.0).budgetSpent(360000000.0)
                    .status("IN_PROGRESS").completionPercentage(30)
                    .startDate("2024-01-01").expectedCompletionDate("2026-12-31")
                    .sanctioningAuthority("Ministry of Tourism / ASI / Karnataka Tourism Dept")
                    .contractor("KSTDC + ASI Empanelled Contractors").sourceLink("https://karnatakatourism.org/").build());

        // Ramanagara District
        if (!projectRepo.existsById("PRJ055"))
            projectRepo.save(Project.builder().id("PRJ055").name("Bengaluru-Mysuru Expressway Service Roads & Flyovers")
                    .category("ROAD").description("Construction of service roads, underpasses and grade separators along the 117 km Bengaluru-Mysuru Expressway for safe local access.")
                    .location("Ramanagara").pincode("562159").district("Ramanagara").state("Karnataka")
                    .budgetAllocated(4500000000.0).budgetSpent(2700000000.0)
                    .status("IN_PROGRESS").completionPercentage(60)
                    .startDate("2023-04-01").expectedCompletionDate("2025-09-30")
                    .sanctioningAuthority("NHAI / Ministry of Road Transport & Highways")
                    .contractor("Dilip Buildcon Ltd").sourceLink("https://nhai.gov.in/").build());

        if (!projectRepo.existsById("PRJ056"))
            projectRepo.save(Project.builder().id("PRJ056").name("Ramanagara Silk Reeling & Cocoon Market Modernisation")
                    .category("INDUSTRY").description("Modernisation of Ramanagara Central Silk Cocoon Market and 60 silk reeling units with automated machines, storage and market linkage.")
                    .location("Ramanagara").pincode("562159").district("Ramanagara").state("Karnataka")
                    .budgetAllocated(850000000.0).budgetSpent(255000000.0)
                    .status("IN_PROGRESS").completionPercentage(30)
                    .startDate("2024-05-01").expectedCompletionDate("2026-04-30")
                    .sanctioningAuthority("Central Silk Board / Karnataka Sericulture Dept")
                    .contractor("Karnataka Silk Industries Corporation").sourceLink("https://www.ksic.in/").build());

        // Chikkaballapur District
        if (!projectRepo.existsById("PRJ057"))
            projectRepo.save(Project.builder().id("PRJ057").name("Kempegowda International Airport Phase 2 - Terminal 2")
                    .category("TRANSPORT").description("New Terminal 2 at Bengaluru KIA with 25 million passenger capacity annually — designed for sustainable, green operations.")
                    .location("Devanahalli, Chikkaballapur").pincode("562149").district("Chikkaballapur").state("Karnataka")
                    .budgetAllocated(130000000000.0).budgetSpent(110000000000.0)
                    .status("IN_PROGRESS").completionPercentage(85)
                    .startDate("2019-05-01").expectedCompletionDate("2025-06-30")
                    .sanctioningAuthority("AAI / BIAL / Ministry of Civil Aviation")
                    .contractor("L&T Construction / Simplex Infrastructures").sourceLink("https://bengaluruairport.com/").build());

        if (!projectRepo.existsById("PRJ058"))
            projectRepo.save(Project.builder().id("PRJ058").name("Chikkaballapur District Horticulture Cold Chain Network")
                    .category("AGRICULTURE").description("Network of 25 cold storage facilities across Chikkaballapur for tomato, potato and flower growers with reefer van connectivity to Bengaluru markets.")
                    .location("Chikkaballapur").pincode("562101").district("Chikkaballapur").state("Karnataka")
                    .budgetAllocated(1200000000.0).budgetSpent(480000000.0)
                    .status("IN_PROGRESS").completionPercentage(40)
                    .startDate("2024-01-01").expectedCompletionDate("2025-12-31")
                    .sanctioningAuthority("Ministry of Food Processing / Karnataka Horticulture Dept")
                    .contractor("National Horticulture Board + SFAC").sourceLink("https://nhb.gov.in/").build());

        // Uttara Kannada District
        if (!projectRepo.existsById("PRJ059"))
            projectRepo.save(Project.builder().id("PRJ059").name("Karwar Naval Base Expansion - INS Kadamba")
                    .category("INDUSTRY").description("Expansion of Project Seabird at INS Kadamba, Karwar — largest naval base in Asia, with new dry docks, jetties and submarine pens.")
                    .location("Karwar, Uttara Kannada").pincode("581301").district("Uttara Kannada").state("Karnataka")
                    .budgetAllocated(190000000000.0).budgetSpent(110000000000.0)
                    .status("IN_PROGRESS").completionPercentage(58)
                    .startDate("2005-01-01").expectedCompletionDate("2027-12-31")
                    .sanctioningAuthority("Ministry of Defence / Indian Navy")
                    .contractor("L&T Defence + Hindustan Shipyard Ltd").sourceLink("https://indiannavy.nic.in/").build());

        if (!projectRepo.existsById("PRJ060"))
            projectRepo.save(Project.builder().id("PRJ060").name("Gokarna-Murdeshwar Coastal Tourism Circuit")
                    .category("TOURISM").description("Comprehensive tourism infrastructure development — access roads, beach promenade, pilgrimage facilities, heritage walks and eco-stays along the Uttara Kannada coastline.")
                    .location("Gokarna, Uttara Kannada").pincode("581326").district("Uttara Kannada").state("Karnataka")
                    .budgetAllocated(900000000.0).budgetSpent(270000000.0)
                    .status("IN_PROGRESS").completionPercentage(30)
                    .startDate("2024-06-01").expectedCompletionDate("2026-09-30")
                    .sanctioningAuthority("Karnataka Tourism Dept / PRASAD Scheme / Ministry of Tourism")
                    .contractor("KSTDC + Uttara Kannada Zilla Panchayat").sourceLink("https://karnatakatourism.org/").build());

        log.info("Projects seeded. Total in DB: {}", projectRepo.count());
    }
}