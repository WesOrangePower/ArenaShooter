package agency.shitcoding.arena.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class Faq {

  private final String title;
  private final String content;

  public Book getBook() {
    Component titleComponent = Component.text(title);
    Component author = Component.text("FAQ");
    List<Component> pages = splitComponents(content);

    return Book.book(titleComponent, author, pages);
  }

  private List<Component> splitComponents(String content) {
    final int charPerPage = 200; // 256 full chars
    List<Component> pages = new ArrayList<>(16);
    String[] s = content.split(" ");
    int chars = 0;
    StringBuilder sb = new StringBuilder();
    for (String word : s) {
      if (chars + word.length() + 1 >= charPerPage) {
        chars = 0;
        pages.add(Component.text(sb.toString()));
        sb = new StringBuilder();
        continue;
      }
      chars += word.length() + 1;
      sb.append(word).append(' ');
    }
    if (!sb.isEmpty()) {
      pages.add(Component.text(sb.toString()));
    }

    return pages;
  }
}
