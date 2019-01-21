
export class HtmlUtil {
    private static entityMap = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': '&quot;',
        "'": '&#39;',
        "/": '&#x2F;'
    };

    static escapeHtml(source: string): string {
        return String(source).replace(/[&<>"'\/]/g, s => HtmlUtil.entityMap[s]);
    }
}
