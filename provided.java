public class provided {
    public static void main(String[] args) {
        try {
            String port = System.getenv("PORT");
            if (port == null || port.isEmpty()) {
                port = "8080";
            }
            ProcessBuilder pb = new ProcessBuilder("java", "-Dserver.port=" + port, "-jar", "/app/app.jar");
            pb.inheritIO();
            Process p = pb.start();
            System.exit(p.waitFor());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
