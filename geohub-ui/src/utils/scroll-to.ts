function easeInOutQuad(t: number, b: number, c: number, d: number): number {
  t /= d / 2;
  if (t < 1) {
    return c / 2 * t * t + b;
  }
  t--;
  return -c / 2 * (t * (t - 2) - 1) + b;
}
function requestAnimFrame(callback: FrameRequestCallback): number {
  return (
      window.requestAnimationFrame ||
      function(callback: FrameRequestCallback) {
        window.setTimeout(callback, 1000 / 60);
      }
  )(callback);
}

function move(amount: number): void {
  document.documentElement.scrollTop = amount;
  document.body.scrollTop = amount;
}

function position(): number {
  return (
      document.documentElement.scrollTop ||
      document.body.scrollTop
  );
}

export function scrollTo(to: number, duration: number = 500, callback?: () => void): void {
  const start = position();
  const change = to - start;
  const increment = 20;
  let currentTime = 0;

  const animateScroll = function(): void {
    currentTime += increment;
    const val = easeInOutQuad(currentTime, start, change, duration);
    move(val);

    if (currentTime < duration) {
      requestAnimFrame(animateScroll);
    } else {
      if (callback) {
        callback();
      }
    }
  };

  animateScroll();
}