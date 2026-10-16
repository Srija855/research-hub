package texthack.engine.m1_foundation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import texthack.engine.ds.CustomArrayList;

/**
 * Hand-built Corpus and Data File Loader.
 * Zero java.util.* usage.
 */
public class CorpusLoader {

    public static class Document {
        public String id;
        public String title;
        public String language;
        public String topic;
        public String content;

        public Document(String id, String title, String language, String topic, String content) {
            this.id = id;
            this.title = title;
            this.language = language;
            this.topic = topic;
            this.content = content;
        }

        @Override
        public String toString() {
            return "[" + id + "] " + title + " (" + language + " | " + topic + ")";
        }
    }

    public static class CitationEdge {
        public String fromDoc;
        public String toDoc;
        public int capacity;
        public int unitCost;

        public CitationEdge(String fromDoc, String toDoc, int capacity, int unitCost) {
            this.fromDoc = fromDoc;
            this.toDoc = toDoc;
            this.capacity = capacity;
            this.unitCost = unitCost;
        }
    }

    public static class ProjectData {
        public String id;
        public String name;
        public int profit;
        public int execTime;
        public int deadline;
        public CustomArrayList<String> dependencies;

        public ProjectData(String id, String name, int profit, int execTime, int deadline, CustomArrayList<String> dependencies) {
            this.id = id;
            this.name = name;
            this.profit = profit;
            this.execTime = execTime;
            this.deadline = deadline;
            this.dependencies = dependencies;
        }
    }

    public static class PrimeTestData {
        public long number;
        public String status;
        public String description;

        public PrimeTestData(long number, String status, String description) {
            this.number = number;
            this.status = status;
            this.description = description;
        }
    }

    public static CustomArrayList<Document> loadCorpus(String path) {
        CustomArrayList<Document> list = new CustomArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("Warning: Corpus file not found at " + path);
            return list;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = splitManual(line, '|');
                if (parts.length >= 5) {
                    list.add(new Document(parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading corpus: " + e.getMessage());
        }
        return list;
    }

    public static CustomArrayList<CitationEdge> loadCitations(String path) {
        CustomArrayList<CitationEdge> list = new CustomArrayList<>();
        File file = new File(path);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = splitManual(line, '|');
                if (parts.length >= 4) {
                    list.add(new CitationEdge(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading citations: " + e.getMessage());
        }
        return list;
    }

    public static CustomArrayList<ProjectData> loadProjects(String path) {
        CustomArrayList<ProjectData> list = new CustomArrayList<>();
        File file = new File(path);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = splitManual(line, '|');
                if (parts.length >= 6) {
                    CustomArrayList<String> deps = new CustomArrayList<>();
                    if (!parts[5].equalsIgnoreCase("NONE")) {
                        String[] depParts = splitManual(parts[5], ',');
                        for (int i = 0; i < depParts.length; i++) {
                            deps.add(depParts[i].trim());
                        }
                    }
                    list.add(new ProjectData(parts[0], parts[1], Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), deps));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading projects: " + e.getMessage());
        }
        return list;
    }

    public static CustomArrayList<PrimeTestData> loadPrimes(String path) {
        CustomArrayList<PrimeTestData> list = new CustomArrayList<>();
        File file = new File(path);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = splitManual(line, '|');
                if (parts.length >= 3) {
                    try {
                        long num = Long.parseLong(parts[0]);
                        list.add(new PrimeTestData(num, parts[1], parts[2]));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading primes: " + e.getMessage());
        }
        return list;
    }

    /**
     * Hand-built string splitting helper to avoid java.util.regex or external classes.
     */
    public static String[] splitManual(String text, char delimiter) {
        int count = 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == delimiter) count++;
        }
        String[] result = new String[count];
        int start = 0;
        int idx = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == delimiter) {
                result[idx++] = text.substring(start, i);
                start = i + 1;
            }
        }
        result[idx] = text.substring(start);
        return result;
    }
}
