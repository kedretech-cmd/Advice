package com.example.data.local

import com.example.data.model.AdviceCategory
import com.example.data.model.DailyAdviceEntity
import com.example.util.EthiopianDateHelper

object InitialSeedData {

    fun getInitialTenAdvice(date: String = EthiopianDateHelper.getTodayIsoDate()): List<DailyAdviceEntity> {
        val ethDate = EthiopianDateHelper.getTodayEthiopianString()

        return listOf(
            DailyAdviceEntity(
                id = "${date}_01",
                title = "የራስህን አቅም አትናቅ",
                advice = "ስኬት የሚጀምረው በራስ መተማመን ነው። ማንም ሰው ምንም ቢልህ በውስጥህ ያለው አቅም ተዓምር የመሥራት ኃይል አለው። ትልልቅ የዓለማችን ስኬቶች የተገኙት በትናንሽ ጽናቶችና በማያቋርጥ ጥረት ነው። ዛሬ የምትወስደው አንድ ትንሽ እርምጃ ለነገው ማንነትህ ታላቅ መሠረት ይሆናል። ፍርሃትን ወደጎን ትተህ በራስህ እመን፤ ድል ያንተ ናት።",
                explanation = "በራስ መተማመን የውስጥ ኃይልህን አውጥተህ እንድትጠቀም እና ለሚገጥሙህ ፈተናዎች እጅ እንዳትሰጥ ያደርግሃል።",
                dailyAction = "ዛሬ ለረጅም ጊዜ ስትፈራው የነበረውን አንድ ጠቃሚ ሥራ ጀምር።",
                category = AdviceCategory.MOTIVATION.amharicName,
                categoryKey = AdviceCategory.MOTIVATION.key,
                imagePrompt = "A determined young Ethiopian person standing on a lush green mountain hill looking at the sunrise with hope and confidence",
                publicationDate = date,
                ethiopianDate = ethDate
            ),
            DailyAdviceEntity(
                id = "${date}_02",
                title = "ዕለታዊ የንባብ ልማድ አዳብር",
                advice = "እውቀት የማይነጥፍ የሀብት ምንጭ ነው። በቀን ቢያንስ 20 ደቂቃ ጠቃሚ መጽሐፍ ማንበብ አእምሮህን ያሰፋል፣ አዳዲስ አመለካከቶችን ይከፍትልሃል። የትላንት እውቀትህ የዛሬውን ዓለም ለመምራት በቂ አይደለም። የማያቋርጥ ንባብ እና አዲስ ክህሎት መቅሰም በየትኛውም የሕይወት መስክ ቀዳሚ እንድትሆን ያደርግሃል።",
                explanation = "መጽሐፍ ማንበብ የአስተሳሰብ አድማስህን ያሰፋል፣ የማስታወስ ችሎታን ያጎለብታል እንዲሁም የሕይወት ጥበብን በነፃ ያስተምርሃል።",
                dailyAction = "ዛሬ ቢያንስ 10 ገጽ ጠቃሚ የዕውቀት ወይም የክህሎት መጽሐፍ አንብብ።",
                category = AdviceCategory.EDUCATION.amharicName,
                categoryKey = AdviceCategory.EDUCATION.key,
                imagePrompt = "An open antique book bathed in warm sunlight on a rustic wooden table with an Ethiopian coffee cup nearby",
                publicationDate = date,
                ethiopianDate = ethDate
            ),
            DailyAdviceEntity(
                id = "${date}_03",
                title = "ንግድን በትንሹ ጀምር፤ በጥራት አሳድግ",
                advice = "ሥራ ለመጀመር ትልቅ ካፒታል ብቻ መጠበቅ ስህተት ነው። በምትችለው አነስተኛ ገንዘብ እና ባለው ዕድል ሥራህን ጀምር። ለደንበኞችህ ታማኝ ሁን፣ ጥራትን ቅድሚያ ስጥ። የሰው ፍላጎት መፍታት የንግድ ዋና መሠረት ነው። ወጪህን በአግባቡ ተቆጣጠር፣ ትርፍህን መልሰህ በሥራው ላይ ኢንቨስት አድርግ።",
                explanation = "ንግድ በትዕግሥት፣ በጥራትና በደንበኞች እምነት ላይ ሲገነባ ዘላቂና አትራፊ ይሆናል።",
                dailyAction = "ያለህን የንግድ ሃሳብ በወረቀት ላይ በግልጽ አስፍረህ የመጀመሪያውን ወጪ አስላ።",
                category = AdviceCategory.BUSINESS.amharicName,
                categoryKey = AdviceCategory.BUSINESS.key,
                imagePrompt = "A modern Ethiopian young entrepreneur working on a business plan with glowing market growth charts",
                publicationDate = date,
                ethiopianDate = ethDate
            ),
            DailyAdviceEntity(
                id = "${date}_04",
                title = "ዲጂታል ክህሎቶችን ተማር",
                advice = "ዛሬ ዓለም በቴክኖሎጂ እየተመራች ነው። ስልክህን ለጊዜ ማባከን ብቻ ሳይሆን አዳዲስ ዲጂታል ክህሎቶችን ለመማር ተጠቀምበት። የመረጃ አያያዝ፣ የዲጂታል ግብይት እና መሠረታዊ የኮምፒውተር ዕውቀት የትም ቦታ ተፈላጊ ያደርጉሃል። ቴክኖሎጂን የተረዳ ሰው በዘመኑ ገበያ ውስጥ ተፎካካሪ ይሆናል።",
                explanation = "ቴክኖሎጂ የሥራ ዕድሎችን የሚያሰፋ እና ምርታማነትን የሚያሳድግ ዘመናዊ መሣሪያ ነው።",
                dailyAction = "በዩቲዩብ ወይም በኢንተርኔት አንድ ጠቃሚ አዲስ የቴክኖሎጂ አጠቃቀም ቪዲዮ ተመልከት።",
                category = AdviceCategory.TECHNOLOGY.amharicName,
                categoryKey = AdviceCategory.TECHNOLOGY.key,
                imagePrompt = "Futuristic digital glowing interface with code and smart icons illuminating a modern workspace",
                publicationDate = date,
                ethiopianDate = ethDate
            ),
            DailyAdviceEntity(
                id = "${date}_05",
                title = "ስሜትህን የመቆጣጠር ጥበብ",
                advice = "እውነተኛ ብርታት ቁጣንና ድንገተኛ ስሜትን በዕውቀት ማሸነፍ ነው። በአስቸጋሪ ሁኔታዎች ውስጥ እንኳን ረጋ ብለህ አስብ። ስሜትህ ውሳኔህን እንዳያበላሸው ጥንቃቄ አድርግ። ሰዎች በሚያደርጉት ነገር ሳይሆን አንተ በምትሰጠው ምላሽ ላይ ትኩረት አድርግ። ውስጣዊ ሰላምህ ከሁሉ በላይ ውድ ነው።",
                explanation = "ስሜታዊ ሚዛናዊነት ጤናማ ውሳኔዎችን እንድትወስንና ከአላስፈላጊ ፀፀት እንድትድን ያደርግሃል።",
                dailyAction = "ዛሬ የሚያበሳጭ ነገር ሲገጥምህ መልስ ከመስጠትህ በፊት ለ10 ሰከንድ በጥልቀት ተንፍስ።",
                category = AdviceCategory.PERSONAL_DEV.amharicName,
                categoryKey = AdviceCategory.PERSONAL_DEV.key,
                imagePrompt = "Serene person meditating under an ancient Ethiopian wanza tree with tranquil blue sky",
                publicationDate = date,
                ethiopianDate = ethDate
            ),
            DailyAdviceEntity(
                id = "${date}_06",
                title = "ማዳመጥን ቅድሚያ ስጥ",
                advice = "በማህበራዊ ሕይወት ውስጥ መልካም ግንኙነት የሚመሠረተው በመደማመጥ ነው። ሰዎች እንዲረዱህ ከመፈለግህ በፊት ሌሎችን ለመረዳት ሞክር። በትኩረት የሚያዳምጥ ሰው የሌሎችን ልብ ይገዛል፤ ብዙ እውቀትም ይሰበስባል። አላስፈላጊ ክርክርን ትተህ ለሰዎች እውነተኛ አክብሮትና ፍቅር ስጥ።",
                explanation = "መልካም ማዳመጥ እውነተኛ ወዳጅነትን፣ መተማመንንና የቤተሰብ ሰላምን ያጠናክራል።",
                dailyAction = "ዛሬ ከቤተሰብህ ወይም ከጓደኛህ ጋር ስታወራ ሳታቋርጥ ሙሉ በሙሉ አዳምጠው።",
                category = AdviceCategory.RELATIONSHIPS.amharicName,
                categoryKey = AdviceCategory.RELATIONSHIPS.key,
                imagePrompt = "Warm friendly Ethiopian conversation over a traditional Jebena Buna coffee ceremony",
                publicationDate = date,
                ethiopianDate = ethDate
            ),
            DailyAdviceEntity(
                id = "${date}_07",
                title = "ትዕግሥት የጥበብ ሁሉ ቁልፍ ናት",
                advice = "የሚዘራ ዘር ወዲያው አያፈራም፤ ጊዜ ይፈልጋል። በሕይወትህ ለምታደርገው ጥረት ወዲያውኑ ውጤት ባታይ ተስፋ አትቁረጥ። ጠንካራ ዛፍ ሥሩን ወደ መሬት ለማስገባት ጊዜ ይወስድበታል። በትዕግሥትና በርትተህ ከሠራህ የደከምክበት ፍሬ በጊዜው ያማረ ሆኖ ይደርሳል።",
                explanation = "ትዕግሥት ያለመሰልቸት በርትቶ መሥራት እንጂ ዝም ብሎ መቀመጥ አይደለም።",
                dailyAction = "ውጤቱ ለዘገየብህ አንድ ጉዳይ ተስፋ ሳትቆርጥ ተጨማሪ ጥረት አድርግ።",
                category = AdviceCategory.LIFE_WISDOM.amharicName,
                categoryKey = AdviceCategory.LIFE_WISDOM.key,
                imagePrompt = "A majestic old baobab tree thriving in rich earth under golden sunset rays",
                publicationDate = date,
                ethiopianDate = ethDate
            ),
            DailyAdviceEntity(
                id = "${date}_08",
                title = "የቀንህን ቅደም ተከተል አቅድ",
                advice = "የማይተካው የሕይወት ሀብት ጊዜ ነው። ማለዳ ስትነሳ የቀንህን ዋና ዋና ተግባራት ለይተህ ጻፍ። በአንድ ጊዜ በርካታ ሥራዎችን ለመሥራት ከመሞከር ይልቅ በጣም አስፈላጊ በሆነው ሥራ ላይ ሙሉ ትኩረትህን አድርግ። ማዘግየትን አስወግድ፤ የዛሬን ሥራ ለነገ አታስተላልፍ።",
                explanation = "ዕቅድ ማውጣት ጭንቀትን ይቀንሳል፣ ምርታማነትን በእጥፍ ይጨምራል።",
                dailyAction = "ዛሬ መሥራት ያለብህን 3 ዋና ዋና ተግባራት በቅደም ተከተል ጽፈህ አጠናቅቅ።",
                category = AdviceCategory.PRODUCTIVITY.amharicName,
                categoryKey = AdviceCategory.PRODUCTIVITY.key,
                imagePrompt = "Clean modern desk with notebook checklist and hourglass bathed in morning light",
                publicationDate = date,
                ethiopianDate = ethDate
            ),
            DailyAdviceEntity(
                id = "${date}_09",
                title = "ሰውነትህን ተንከባከብ",
                advice = "ጤና የሕይወትህ ሁሉ መሠረት ናት። ያለ ጤና ያገኘኸው ስኬት ትርጉም አይኖረውም። በቂ ንፁህ ውሃ ጠጣ፣ አዘውትረህ በእግር ተጓዝ፣ የተመጣጠነ ምግብ ተመገብ። በቂ እንቅልፍ ማግኘት አእምሮህ እንዲታደስና ብርቱ እንድትሆን ያደርግሃል። ጤናህን ዛሬ ተንከባከበው፤ ነገ ይጠብቅሃል።",
                explanation = "ቀላል የዕለት ተዕለት ጤናማ ልማዶች ከከባድ ሕመም ይጠብቁሃል፤ የሕይወት ኃይልህንም ያድሳሉ።",
                dailyAction = "ዛሬ ቢያንስ 2 ሊትር ንጹህ ውሃ ጠጣ እና ለ30 ደቂቃ የእግር ጉዞ አድርግ።",
                category = AdviceCategory.HEALTHY_HABITS.amharicName,
                categoryKey = AdviceCategory.HEALTHY_HABITS.key,
                imagePrompt = "Fresh natural organic fruits, green herbs, and crystal clear water glass in sunlight",
                publicationDate = date,
                ethiopianDate = ethDate
            ),
            DailyAdviceEntity(
                id = "${date}_10",
                title = "ከምታገኘው ገቢ ቆጥብ",
                advice = "የገንዘብ ነፃነት የሚገኘው ከብዙ ገቢ ብቻ ሳይሆን ከአስተዋይ ወጪ ነው። ከምታገኘው ገቢ ላይ ቢያንስ አስር በመቶውን (10%) ለማስቀመጥ ራስህን እሰር። አስፈላጊ ያልሆኑ ወጪዎችን ቀንሰው። ዛሬ የምትቆጥባት ሳንቲም ለነገው ድንገተኛ ችግር ጋሻ፣ ለወደፊት ዕድገትህም መሰላል ትሆንሃለች።",
                explanation = "መቆጠብ የራስህን የወደፊት እጣ ፈንታ በእጅህ እንድትይዝ እና በሰዎች ላይ ጥገኛ እንዳትሆን ያደርግሃል።",
                dailyAction = "ዛሬ ካወጣኸው ወጪ ውስጥ አንዱን አላስፈላጊ ወጪ ቆርጠህ ወደ ቁጠባ ጨምር።",
                category = AdviceCategory.FINANCIAL.amharicName,
                categoryKey = AdviceCategory.FINANCIAL.key,
                imagePrompt = "A golden seedling growing out of a stack of shiny coins symbolizing financial growth",
                publicationDate = date,
                ethiopianDate = ethDate
            )
        )
    }
}
